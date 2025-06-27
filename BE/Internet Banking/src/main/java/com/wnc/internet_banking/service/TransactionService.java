package com.wnc.internet_banking.service;

import com.wnc.internet_banking.dto.request.transaction.ConfirmDebtPaymentRequest;
import com.wnc.internet_banking.dto.request.transaction.ConfirmTransactionRequest;
import com.wnc.internet_banking.dto.request.transaction.DebtPaymentRequest;
import com.wnc.internet_banking.dto.request.transaction.TransferRequest;
import com.wnc.internet_banking.dto.response.transaction.TransactionDto;
import com.wnc.internet_banking.entity.Transaction;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TransactionService {

    UUID initiateTransfer(TransferRequest transferRequest, UUID userId);

    void confirmTransfer(UUID transactionId, ConfirmTransactionRequest confirmTransactionRequest, UUID userId);

    UUID initiateDebtPayment(DebtPaymentRequest debtPaymentRequest, UUID userId);

    void confirmDebtPayment(UUID transactionId, ConfirmDebtPaymentRequest confirmDebtPaymentRequest, UUID userId);

    List<Transaction> getTransactionHistory(String accountNumber);

    Page<TransactionDto> getTransferTransactionsByUser(UUID userId, int page, int size);

    Page<TransactionDto> getReceivedTransactionsByUser(UUID userId, int page, int size);

    Page<TransactionDto> getDebtPaymentTransactionsByUser(UUID userId, int page, int size);

    Page<TransactionDto> getLinkedBankTransactions(UUID bankId, LocalDateTime startDate, LocalDateTime endDate, int page, int size);
}
