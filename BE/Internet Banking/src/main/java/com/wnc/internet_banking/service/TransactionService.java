package com.wnc.internet_banking.service;

import com.wnc.internet_banking.dto.request.transaction.ConfirmDebtPaymentRequest;
import com.wnc.internet_banking.dto.request.transaction.ConfirmTransactionRequest;
import com.wnc.internet_banking.dto.request.transaction.DebtPaymentRequest;
import com.wnc.internet_banking.dto.request.transaction.InternalTransferRequest;
import com.wnc.internet_banking.entity.Transaction;

import java.util.List;
import java.util.UUID;

public interface TransactionService {

    UUID initiateInternalTransfer(InternalTransferRequest internalTransferRequest, UUID userId);

    void confirmInternalTransfer(UUID transactionId, ConfirmTransactionRequest confirmTransactionRequest, UUID userId);

    UUID initiateDebtPayment(DebtPaymentRequest debtPaymentRequest, UUID userId);

    void confirmDebtPayment(UUID transactionId, ConfirmDebtPaymentRequest confirmDebtPaymentRequest, UUID userId);

    List<Transaction> getTransactionHistory(String accountNumber);
}
