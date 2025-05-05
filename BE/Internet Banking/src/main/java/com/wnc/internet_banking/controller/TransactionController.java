package com.wnc.internet_banking.controller;

import com.wnc.internet_banking.dto.request.transaction.ConfirmTransactionRequest;
import com.wnc.internet_banking.dto.request.transaction.InternalTransferRequest;
import com.wnc.internet_banking.dto.response.BaseResponse;
import com.wnc.internet_banking.service.TransactionService;
import com.wnc.internet_banking.util.SecurityUtil;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/internal-transfers")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BaseResponse<UUID>> initiateInternalTransfer (
            @RequestBody InternalTransferRequest internalTransferRequest
    ) {
       UUID userId = SecurityUtil.getCurrentUserId();

       UUID transactionId = transactionService.initiateInternalTransfer(internalTransferRequest, userId);

       return ResponseEntity.ok(BaseResponse.data(transactionId));
    }

    @PostMapping("/internal-transfers/confirm/{transactionId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<BaseResponse<?>> confirmInternalTransfer (
            @PathVariable UUID transactionId,
            @RequestBody ConfirmTransactionRequest confirmTransactionRequest
    ) {
        UUID userId = SecurityUtil.getCurrentUserId();

        transactionService.confirmInternalTransfer(transactionId, confirmTransactionRequest, userId);

        return ResponseEntity.ok(BaseResponse.message("Transaction confirmed successfully"));
    }
}
