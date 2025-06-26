package com.wnc.internet_banking.service.impl;

import com.wnc.internet_banking.dto.request.transaction.ConfirmDebtPaymentRequest;
import com.wnc.internet_banking.dto.request.transaction.ConfirmTransactionRequest;
import com.wnc.internet_banking.dto.request.transaction.DebtPaymentRequest;
import com.wnc.internet_banking.dto.request.transaction.TransferRequest;
import com.wnc.internet_banking.dto.response.debtreminder.DebtReminderDto;
import com.wnc.internet_banking.dto.response.transaction.TransactionDto;
import com.wnc.internet_banking.entity.*;
import com.wnc.internet_banking.repository.*;
import com.wnc.internet_banking.service.LinkedBankService;
import com.wnc.internet_banking.service.TransactionService;
import com.wnc.internet_banking.util.EmailTemplate;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service("transactionService")
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    private final AccountRepository accountRepository;

    private final UserRepository userRepository;

    private final OtpServiceImpl otpService;

    private final EmailServiceImpl emailService;

    private final DebtReminderServiceImpl debtReminderService;

    private final ModelMapper modelMapper;

    private final LinkedBankRepository linkedBankRepository;

    private final LinkedBankService linkedBankService;

    // Create transaction object and send otp
    private Transaction createTransactionAndSendOtp(
            String receiverAccountNumber,
            String receiverBankCode,
            Double amount,
            String content,
            Transaction.Type transactionType,
            UUID senderUserId
    ) {
        User sender = userRepository.findById(senderUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Account senderAccount = accountRepository.findByUser(sender)
                .orElseThrow(() -> new IllegalArgumentException("Invalid sender account"));

        LinkedBank linkedBank = null;
        if (receiverBankCode == null || receiverBankCode.equals("SCB")) {
            Account receiverAccount = accountRepository.findByAccountNumber(receiverAccountNumber)
                    .orElseThrow(() -> new IllegalArgumentException("Receiver account not found"));
            if (receiverAccount.getAccountNumber().equals(senderAccount.getAccountNumber())) {
                throw new IllegalArgumentException("Receiver account must be different from sender account.");
            }
        } else {
            linkedBank = linkedBankRepository.findByBankCode(receiverBankCode)
                    .orElseThrow(() -> new IllegalArgumentException("Unknown bank with bank code: " + receiverBankCode));

            try {
                linkedBankService.getAccountInfo(receiverBankCode, receiverAccountNumber);
            } catch (Exception e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }

        // Check if the sender and receiver accounts are different
        if (senderAccount.getAccountNumber().equals(receiverAccountNumber)) {
            throw new IllegalArgumentException("Sender and receiver accounts cannot be the same");
        }

        Transaction transaction = Transaction.builder()
                .senderAccountNumber(senderAccount.getAccountNumber())
                .receiverAccountNumber(receiverAccountNumber)
                .receiverBank(linkedBank)
                .amount(amount)
                .fee(0.0)
                .feePayer(Transaction.FeePayer.SENDER)
                .content(content)
                .type(transactionType)
                .status(Transaction.Status.PENDING)
                .build();

        transactionRepository.save(transaction);

        // Send OTP to sender's email
        Otp otp = otpService.generateOtp(senderUserId, Otp.Purpose.TRANSACTION);
        try {
            String emailBody = EmailTemplate.confirmTransaction(sender.getFullName(), otp.getOtpCode());
            emailService.sendEmail(sender.getEmail(), "Transaction confirmation", emailBody);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email");
        }

        return transaction;
    }


    private void confirmTransaction(UUID transactionId, String otpCode, UUID userId) {
        if (!otpService.verifyOtp(userId, otpCode, Otp.Purpose.TRANSACTION)) {
            throw new IllegalArgumentException("Invalid OTP");
        }

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));

        // Check if the transaction is already confirmed or failed
        if (transaction.getStatus() != Transaction.Status.PENDING) {
            throw new IllegalArgumentException("Invalid transaction");
        }

        // Check if the sender account has enough balance
        Account senderAccount = accountRepository.findByAccountNumber(transaction.getSenderAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException("Sender account not found"));

        if (senderAccount.getBalance() < transaction.getAmount()) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        senderAccount.setBalance(senderAccount.getBalance() - transaction.getAmount());

        if (transaction.getReceiverBank() == null) {
            Account receiverAccount = accountRepository.findByAccountNumber(transaction.getReceiverAccountNumber())
                    .orElseThrow(() -> new IllegalArgumentException("Receiver account not found"));
            receiverAccount.setBalance(receiverAccount.getBalance() + transaction.getAmount());

            accountRepository.save(receiverAccount);
        } else {
            try {
                linkedBankService.sendTransferRequest(transaction);
            } catch (Exception e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }

        // Update transaction status
        transaction.setStatus(Transaction.Status.COMPLETED);
        transaction.setConfirmedAt(LocalDateTime.now());

        accountRepository.save(senderAccount);
        transactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public UUID initiateTransfer(TransferRequest transferRequest, UUID userId) {
        // Create new transaction and send otp
        Transaction transaction = createTransactionAndSendOtp(
                transferRequest.getReceiverAccountNumber(),
                transferRequest.getReceiverBankCode(),
                transferRequest.getAmount(),
                transferRequest.getContent(),
                Transaction.Type.MONEY_TRANSFER,
                userId
        );

        return transaction.getTransactionId();
    }

    @Override
    @Transactional
    public void confirmTransfer(UUID transactionId, ConfirmTransactionRequest confirmTransactionRequest, UUID userId) {
        confirmTransaction(transactionId, confirmTransactionRequest.getOtpCode(), userId);
    }

    @Override
    @Transactional
    public UUID initiateDebtPayment(
            DebtPaymentRequest debtPaymentRequest,
            UUID userId
    ) {
        // Check if Debt Reminder is already paid
        if (debtReminderService.isDebtReminderAlreadyPaid(debtPaymentRequest.getDebtReminderId())) {
            throw new IllegalArgumentException("Debt reminder already paid");
        }

        Account creditor = accountRepository.findByAccountNumber(debtPaymentRequest.getCreditorAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException("Creditor account not found"));

        DebtReminderDto debtReminderDto = debtReminderService.getDebtReminderById(debtPaymentRequest.getDebtReminderId());

        // Create new transaction and send otp
        Transaction transaction = createTransactionAndSendOtp(
                creditor.getAccountNumber(),
                null,
                debtReminderDto.getAmount(),
                debtPaymentRequest.getContent(),
                Transaction.Type.DEBT_PAYMENT,
                userId
        );

        return transaction.getTransactionId();
    }

    @Override
    @Transactional
    public void confirmDebtPayment(
            UUID transactionId,
            ConfirmDebtPaymentRequest confirmDebtPaymentRequest,
            UUID userId
    ) {
        confirmTransaction(transactionId, confirmDebtPaymentRequest.getOtpCode(), userId);

        debtReminderService.confirmDebtPayment(confirmDebtPaymentRequest.getDebtReminderId());
    }

    @Override
    public List<Transaction> getTransactionHistory(String accountNumber) {
        return transactionRepository.findBySenderAccountNumberOrReceiverAccountNumber(accountNumber, accountNumber);

    }

    @Override
    public Page<TransactionDto> getTransferTransactionsByUser(UUID userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Account account = accountRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Account not found for user"));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        Page<Transaction> transactions = transactionRepository
                .findBySenderAccountNumberAndTypeAndCreatedAtAfter(
                        account.getAccountNumber(),
                        Transaction.Type.MONEY_TRANSFER,
                        thirtyDaysAgo,
                        pageable
                );

        return transactions.map(transaction -> modelMapper.map(transaction, TransactionDto.class));
    }

    @Override
    public Page<TransactionDto> getReceivedTransactionsByUser(UUID userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Account account = accountRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Account not found for user"));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        Page<Transaction> transactions = transactionRepository
                .findByReceiverAccountNumberAndCreatedAtAfter(
                        account.getAccountNumber(),
                        thirtyDaysAgo,
                        pageable
                );

        return transactions.map(transaction -> modelMapper.map(transaction, TransactionDto.class));
    }

    @Override
    public Page<TransactionDto> getDebtPaymentTransactionsByUser(UUID userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Account account = accountRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Account not found for user"));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        Page<Transaction> transactions = transactionRepository
                .findBySenderAccountNumberAndTypeAndCreatedAtAfter(
                        account.getAccountNumber(),
                        Transaction.Type.DEBT_PAYMENT,
                        thirtyDaysAgo,
                        pageable
                );

        return transactions.map(transaction -> modelMapper.map(transaction, TransactionDto.class));
    }
}
