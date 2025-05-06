package com.wnc.internet_banking.controller;

import com.wnc.internet_banking.dto.response.BaseResponse;
import com.wnc.internet_banking.entity.Transaction;
import com.wnc.internet_banking.entity.User;
import com.wnc.internet_banking.repository.UserRepository;
import com.wnc.internet_banking.service.TransactionService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;
    private final UserRepository userRepository;

    public TransactionController(TransactionService transactionService, UserRepository userRepository) {
        this.transactionService = transactionService;
        this.userRepository = userRepository;
    }


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
}
