package com.wnc.internet_banking.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wnc.internet_banking.dto.request.transaction.LinkedBankTransferRequestDto;
import com.wnc.internet_banking.dto.response.linkedbank.AccountResponseDto;
import com.wnc.internet_banking.dto.response.linkedbank.LinkedBankDto;
import com.wnc.internet_banking.entity.Account;
import com.wnc.internet_banking.entity.LinkedBank;
import com.wnc.internet_banking.entity.Transaction;
import com.wnc.internet_banking.repository.AccountRepository;
import com.wnc.internet_banking.repository.LinkedBankRepository;
import com.wnc.internet_banking.repository.TransactionRepository;
import com.wnc.internet_banking.service.LinkedBankService;
import com.wnc.internet_banking.util.HmacUtils;
import com.wnc.internet_banking.util.RSAUtils;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service("linkedBankService")
public class LinkedBankServiceImpl implements LinkedBankService {
    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;
    private final LinkedBankRepository linkedBankRepository;
    private final TransactionRepository transactionRepository;
    private final String ourBankCode = "SCB";

    @Value("${hash.secret-key}")
    private String hashSecretKey;

    @Value("${rsa.private-key}")
    private String privateKey;

    public LinkedBankServiceImpl(AccountRepository accountRepository,
                                 ModelMapper modelMapper,
                                 LinkedBankRepository linkedBankRepository,
                                 TransactionRepository transactionRepository,
                                 @Value("${hash.secret-key}") String hashSecretKey) {
        this.accountRepository = accountRepository;
        this.modelMapper = modelMapper;
        this.linkedBankRepository = linkedBankRepository;
        this.transactionRepository = transactionRepository;
        this.hashSecretKey = hashSecretKey;
    }

    @Override
    public List<LinkedBankDto> getAllLinkedBanks() {
        List<LinkedBank> list = linkedBankRepository.findAll();

        return list.stream()
                .map((linkedBank) -> modelMapper.map(linkedBank, LinkedBankDto.class))
                .toList();
    }

    @Override
    public AccountResponseDto getAccountInfo(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber).orElseThrow(
                () -> new EntityNotFoundException("Account not found with account number: " + accountNumber));

        AccountResponseDto accountDto = new AccountResponseDto();
        accountDto.setAccountNumber(account.getAccountNumber());
        accountDto.setFullName(account.getUser().getFullName());
        return accountDto;
    }

    @Override
    public void transfers(LinkedBankTransferRequestDto requestDto, String bankCode) {
        Account account = accountRepository.findByAccountNumber(requestDto.getReceiverAccountNumber()).orElseThrow(
                () -> new EntityNotFoundException("Account not found with account number: "
                        + requestDto.getReceiverAccountNumber()));

        LinkedBank bank = linkedBankRepository.findByBankCode(bankCode)
                .orElseThrow(() -> new EntityNotFoundException("Unknown bank with bank code: " + bankCode));

        Transaction transaction = Transaction.builder()
                .senderBank(bank)
                .senderAccountNumber(requestDto.getSenderAccountNumber())
                .receiverAccountNumber(requestDto.getReceiverAccountNumber())
                .amount(requestDto.getAmount())
                .fee(0.0)
                .feePayer(Transaction.FeePayer.SENDER)
                .content(requestDto.getContent())
                .type(Transaction.Type.MONEY_TRANSFER)
                .status(Transaction.Status.COMPLETED)
                .build();

        // Save transaction
        transactionRepository.save(transaction);

        // Update account balance
        account.setBalance(account.getBalance() + requestDto.getAmount());
        accountRepository.save(account);
    }

    @Override
    public boolean verifyTimestamp(String timestamp) {
        try {
            long requestTime = Long.parseLong(timestamp);
            long now = Instant.now().toEpochMilli();
            return Math.abs(now - requestTime) < 5 * 60 * 1000; // 5 minutes
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean verifyRequestHash(String rawBody, String timestamp, String bankCode, String requestHash) throws Exception {
        LinkedBank bank = linkedBankRepository.findByBankCode(bankCode)
                .orElseThrow(() -> new EntityNotFoundException("Unknown bank with bank code: " + bankCode));

        String hashInput = rawBody + timestamp + bankCode + hashSecretKey;

        String expectedHash = HmacUtils.hmacSha256(hashInput, hashSecretKey);

        return expectedHash.equals(requestHash);
    }

    @Override
    public boolean verifySignature(String bankCode, String data, String signature) throws Exception {
        LinkedBank bank = linkedBankRepository.findByBankCode(bankCode)
                .orElseThrow(() -> new EntityNotFoundException("Unknown bank with bank code: " + bankCode));

        return RSAUtils.verify(data, signature, bank.getPublicKey());
    }

    @Override
    public AccountResponseDto getAccountInfo(String bankCode, String accountNumber) throws Exception {
        LinkedBank bank = linkedBankRepository.findByBankCode(bankCode)
                .orElseThrow(() -> new IllegalArgumentException("Unknown bank with bank code: " + bankCode));

        // Convert to JSON
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> bodyMap = new LinkedHashMap<>();
        bodyMap.put("accountNumber", accountNumber);
        bodyMap.put("bankCode", ourBankCode);
        String body = mapper.writeValueAsString(bodyMap);

        String now = String.valueOf(Instant.now().toEpochMilli());
        String hmacInput = body + now + ourBankCode + bank.getSecretKeyHash();
        String hmac = HmacUtils.hmacSha256(hmacInput, hashSecretKey);

        // Send request
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-Timestamp", now);
        headers.add("Bank-Code", ourBankCode);
        headers.add("X-Request-Hash", hmac);

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    bank.getGetAccountInfoUrl(), entity, String.class);

            JsonNode root = mapper.readTree(response.getBody());
            JsonNode dataNode = root.path("data");
            String fullName = dataNode.path("fullName").asText();

            AccountResponseDto accountDto = new AccountResponseDto();
            accountDto.setAccountNumber(accountNumber);
            accountDto.setFullName(fullName);
            return accountDto;
        } catch (HttpStatusCodeException ex) {
            String errorBody = ex.getResponseBodyAsString();
            JsonNode root = mapper.readTree(errorBody);
            if (root.path("message").asText().contains("not found")) {
                throw new IllegalArgumentException("Account not found with account number: " + accountNumber);
            } else {
                throw new IllegalArgumentException(root.path("message").asText());
            }
        }
    }

    @Override
    public void sendTransferRequest(Transaction transaction)
            throws Exception {
        String receiverBankCode = transaction.getReceiverBank().getBankCode();
        LinkedBank bank = linkedBankRepository.findByBankCode(receiverBankCode)
                .orElseThrow(() -> new IllegalArgumentException("Unknown bank with bank code: " + receiverBankCode));

        // Convert to JSON
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> bodyMap = new LinkedHashMap<>();
        bodyMap.put("senderAccountNumber", transaction.getSenderAccountNumber());
        bodyMap.put("receiverAccountNumber", transaction.getReceiverAccountNumber());
        bodyMap.put("amount", transaction.getAmount());
        bodyMap.put("content", transaction.getContent());
        String body = mapper.writeValueAsString(bodyMap);

        String now = String.valueOf(Instant.now().toEpochMilli());
        String hmacInput = body + now + ourBankCode + bank.getSecretKeyHash();
        String hmac = HmacUtils.hmacSha256(hmacInput, hashSecretKey);

        String signature = RSAUtils.sign(body, privateKey);

        // Send request
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-Timestamp", now);
        headers.add("Bank-Code", ourBankCode);
        headers.add("X-Request-Hash", hmac);
        headers.add("X-Signature", signature);

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    bank.getMoneyTransferUrl(), entity, String.class);
        } catch (HttpStatusCodeException ex) {
            String errorBody = ex.getResponseBodyAsString();
            JsonNode root = mapper.readTree(errorBody);
            if (root.path("message").asText().contains("not found")) {
                throw new IllegalArgumentException("Account not found with account number: " + transaction.getReceiverAccountNumber());
            } else {
                throw new IllegalArgumentException(root.path("message").asText());
            }
        }
    }
}
