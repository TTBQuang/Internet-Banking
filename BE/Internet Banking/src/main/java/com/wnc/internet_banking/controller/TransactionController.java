package com.wnc.internet_banking.controller;

import com.wnc.internet_banking.dto.request.transaction.ConfirmDebtPaymentRequest;
import com.wnc.internet_banking.dto.request.transaction.ConfirmTransactionRequest;
import com.wnc.internet_banking.dto.request.transaction.DebtPaymentRequest;
import com.wnc.internet_banking.dto.request.transaction.TransferRequest;
import com.wnc.internet_banking.dto.response.BaseResponse;
import com.wnc.internet_banking.dto.response.transaction.TransactionDto;
import com.wnc.internet_banking.entity.Transaction;
import com.wnc.internet_banking.service.TransactionService;
import com.wnc.internet_banking.util.SecurityUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/transactions")
@Tag(name = "Transaction API", description = "Quản lý giao dịch")
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping("/histories/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BaseResponse<List<Transaction>>> getTransactionHistoryForCustomer(
            @RequestParam String accountNumber) {
        List<Transaction> transactions = transactionService.getTransactionHistory(accountNumber);
        return ResponseEntity.ok(BaseResponse.data(transactions));
    }

    @GetMapping("/histories")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<BaseResponse<List<Transaction>>> getTransactionHistoryForEmployee(
            @RequestParam String accountNumber) {
        List<Transaction> transactions = transactionService.getTransactionHistory(accountNumber);
        return ResponseEntity.ok(BaseResponse.data(transactions));
    }

    @GetMapping("/transfer/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BaseResponse<Page<TransactionDto>>> getCurrentUserTransferTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        UUID userId = SecurityUtil.getCurrentUserId();

        Page<TransactionDto> transactions = transactionService.getTransferTransactionsByUser(userId, page, size);

        return ResponseEntity.ok(BaseResponse.data(transactions));
    }

    @GetMapping("/received/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BaseResponse<Page<TransactionDto>>> getCurrentUserReceivedTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        UUID userId = SecurityUtil.getCurrentUserId();

        Page<TransactionDto> transactions = transactionService.getReceivedTransactionsByUser(userId, page, size);

        return ResponseEntity.ok(BaseResponse.data(transactions));
    }

    @GetMapping("/debt-payment/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BaseResponse<Page<TransactionDto>>> getCurrentUserDebtPaymentTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        UUID userId = SecurityUtil.getCurrentUserId();

        Page<TransactionDto> transactions = transactionService.getDebtPaymentTransactionsByUser(userId, page, size);

        return ResponseEntity.ok(BaseResponse.data(transactions));
    }

    @PostMapping("/transfers")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BaseResponse<UUID>> initiateTransfer(
            @RequestBody TransferRequest transferRequest
    ) {
        UUID userId = SecurityUtil.getCurrentUserId();

        UUID transactionId = transactionService.initiateTransfer(transferRequest, userId);

        return ResponseEntity.ok(BaseResponse.data(transactionId));
    }

    @PostMapping("/transfers/{transactionId}/confirm")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BaseResponse<?>> confirmTransfer(
            @PathVariable UUID transactionId,
            @RequestBody ConfirmTransactionRequest confirmTransactionRequest
    ) {
        UUID userId = SecurityUtil.getCurrentUserId();

        transactionService.confirmTransfer(transactionId, confirmTransactionRequest, userId);

        return ResponseEntity.ok(BaseResponse.message("Transaction confirmed successfully"));
    }

    @PostMapping("/debt-payment")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BaseResponse<UUID>> initiateDebtPayment(
            @RequestBody DebtPaymentRequest debtPaymentRequest
    ) {
        UUID userId = SecurityUtil.getCurrentUserId();

        UUID transactionId = transactionService.initiateDebtPayment(debtPaymentRequest, userId);

        return ResponseEntity.ok(BaseResponse.data(transactionId));
    }

    @PostMapping("/debt-payment/{transactionId}/confirm")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BaseResponse<?>> confirmDebtPayment(
            @PathVariable UUID transactionId,
            @RequestBody ConfirmDebtPaymentRequest confirmDebtPaymentRequest
    ) {
        UUID userId = SecurityUtil.getCurrentUserId();

        transactionService.confirmDebtPayment(transactionId, confirmDebtPaymentRequest, userId);

        return ResponseEntity.ok(BaseResponse.message("Transaction confirmed successfully"));
    }
}
