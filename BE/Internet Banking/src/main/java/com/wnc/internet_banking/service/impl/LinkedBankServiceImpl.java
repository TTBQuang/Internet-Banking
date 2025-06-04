package com.wnc.internet_banking.service.impl;

import com.wnc.internet_banking.dto.request.transaction.LinkedBankTransferRequestDto;
import com.wnc.internet_banking.dto.response.account.AccountDto;
import com.wnc.internet_banking.dto.response.linkedbank.AccountResponseDto;
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
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service("linkedBankService")
public class LinkedBankServiceImpl implements LinkedBankService {
    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;
    private final LinkedBankRepository linkedBankRepository;
    private final TransactionRepository transactionRepository;

    @Value("${hash.secret-key}")
    private String hashSecretKey;

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
}
