package com.wnc.internet_banking.service.impl;

import com.wnc.internet_banking.dto.request.transaction.ConfirmDebtPaymentRequest;
import com.wnc.internet_banking.dto.request.transaction.ConfirmTransactionRequest;
import com.wnc.internet_banking.dto.request.transaction.DebtPaymentRequest;
import com.wnc.internet_banking.dto.request.transaction.InternalTransferRequest;
import com.wnc.internet_banking.dto.response.debtreminder.DebtReminderDto;
import com.wnc.internet_banking.dto.response.transaction.TransactionDto;
import com.wnc.internet_banking.entity.Account;
import com.wnc.internet_banking.entity.Otp;
import com.wnc.internet_banking.entity.Transaction;
import com.wnc.internet_banking.entity.User;
import com.wnc.internet_banking.entity.*;
import com.wnc.internet_banking.repository.AccountRepository;
import com.wnc.internet_banking.repository.RecipientRepository;
import com.wnc.internet_banking.repository.TransactionRepository;
import com.wnc.internet_banking.repository.UserRepository;
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

import java.util.List;

import java.time.LocalDateTime;
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
    private final RecipientRepository recipientRepository;

    // Create transaction object and send otp
    private Transaction createTransactionAndSendOtp(
            UUID receiverAccountId,
            Double amount,
            String content,
            Transaction.Type transactionType,
            UUID senderUserId
    ) {
        User sender = userRepository.findById(senderUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Account senderAccount = accountRepository.findByUser(sender)
                .orElseThrow(() -> new IllegalArgumentException("Invalid sender account"));

        Account receiverAccount = accountRepository.findById(receiverAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver account not found"));

        // Check if the sender and receiver accounts are different
        if (senderAccount.getAccountId().equals(receiverAccount.getAccountId())) {
            throw new IllegalArgumentException("Sender and receiver accounts cannot be the same");
        }

        Transaction transaction = Transaction.builder()
                .senderAccountNumber(senderAccount.getAccountNumber())
                .receiverAccountNumber(receiverAccount.getAccountNumber())
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

        Account receiverAccount = accountRepository.findByAccountNumber(transaction.getReceiverAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException("Receiver account not found"));
        receiverAccount.setBalance(receiverAccount.getBalance() + transaction.getAmount());

        // Update transaction status
        transaction.setStatus(Transaction.Status.COMPLETED);
        transaction.setConfirmedAt(LocalDateTime.now());

        accountRepository.save(senderAccount);
        accountRepository.save(receiverAccount);
        transactionRepository.save(transaction);
    }

    @Override
    @Transactional
    public UUID initiateInternalTransfer(InternalTransferRequest internalTransferRequest, UUID userId) {
        Account account = accountRepository.findByAccountNumber(internalTransferRequest.getReceiverAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException("Recipient account not found"));
        // Create new transaction and send otp
        Transaction transaction = createTransactionAndSendOtp(
                account.getAccountId(),
                internalTransferRequest.getAmount(),
                internalTransferRequest.getContent(),
                Transaction.Type.MONEY_TRANSFER,
                userId
        );

        return transaction.getTransactionId();
    }

    @Override
    @Transactional
    public void confirmInternalTransfer(UUID transactionId, ConfirmTransactionRequest confirmTransactionRequest, UUID userId) {
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
                creditor.getAccountId(),
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

        Page<Transaction> transactions = transactionRepository.findBySenderAccountNumberAndType(account.getAccountNumber(), Transaction.Type.MONEY_TRANSFER, pageable);

        return transactions.map(transaction -> modelMapper.map(transaction, TransactionDto.class));
    }

    @Override
    public Page<TransactionDto> getReceivedTransactionsByUser(UUID userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Account account = accountRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Account not found for user"));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Transaction> transactions = transactionRepository.findByReceiverAccountNumber(account.getAccountNumber(), pageable);

        return transactions.map(transaction -> modelMapper.map(transaction, TransactionDto.class));
    }

    @Override
    public Page<TransactionDto> getDebtPaymentTransactionsByUser(UUID userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Account account = accountRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Account not found for user"));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Transaction> transactions = transactionRepository.findBySenderAccountNumberAndType(account.getAccountNumber(), Transaction.Type.DEBT_PAYMENT, pageable);

        return transactions.map(transaction -> modelMapper.map(transaction, TransactionDto.class));
    }
}
