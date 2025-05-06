package com.wnc.internet_banking.service;

import com.wnc.internet_banking.entity.Transaction;
import com.wnc.internet_banking.entity.User;
import com.wnc.internet_banking.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;

    public List<Transaction> getTransactionHistory(String accountNumber) {
        return transactionRepository.findBySenderAccountNumberOrReceiverAccountNumber(accountNumber, accountNumber);

    }

}
