package com.wnc.internet_banking.repository;


import com.wnc.internet_banking.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    // Tìm các giao dịch có liên quan đến accountNumber (gửi hoặc nhận)
    List<Transaction> findBySenderAccountNumberOrReceiverAccountNumber(String senderAccountNumber, String receiverAccountNumber);


}
