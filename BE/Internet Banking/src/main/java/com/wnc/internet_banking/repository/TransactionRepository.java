package com.wnc.internet_banking.repository;


import com.wnc.internet_banking.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    // Tìm các giao dịch có liên quan đến accountNumber (gửi hoặc nhận)
    List<Transaction> findBySenderAccountNumberOrReceiverAccountNumber(String senderAccountNumber, String receiverAccountNumber);

    Page<Transaction> findBySenderAccountNumberAndType(String senderAccountNumber, Transaction.Type type, Pageable pageable);

    Page<Transaction> findByReceiverAccountNumber(String receiverAccountNumber, Pageable pageable);

    @Query("SELECT t FROM Transaction t " +
            "WHERE t.senderBank IS NOT NULL OR t.receiverBank IS NOT NULL")
    Page<Transaction> findAllInterBankTransactions(Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE (t.senderBank IS NOT NULL OR t.receiverBank IS NOT NULL) AND t.createdAt >= :startDate")
    Page<Transaction> findAllInterBankTransactionsByStartDate(@Param("startDate") LocalDateTime startDate, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE (t.senderBank IS NOT NULL OR t.receiverBank IS NOT NULL) AND t.createdAt <= :endDate")
    Page<Transaction> findAllInterBankTransactionsByEndDate(@Param("endDate") LocalDateTime endDate, Pageable pageable);

    @Query("SELECT t FROM Transaction t " +
            "WHERE (t.senderBank IS NOT NULL OR t.receiverBank IS NOT NULL) " +
            "AND t.createdAt >= :startDate AND t.createdAt <= :endDate")
    Page<Transaction> findAllInterBankTransactionsByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    @Query("SELECT t FROM Transaction t " +
            "WHERE t.senderBank.linkedBankId = :bankId OR t.receiverBank.linkedBankId = :bankId")
    Page<Transaction> findByBankId(@Param("bankId") UUID bankId, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE (t.senderBank.linkedBankId = :bankId OR t.receiverBank.linkedBankId = :bankId) AND t.createdAt >= :startDate")
    Page<Transaction> findByBankIdAndStartDate(@Param("bankId") UUID bankId, @Param("startDate") LocalDateTime startDate, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE (t.senderBank.linkedBankId = :bankId OR t.receiverBank.linkedBankId = :bankId) AND t.createdAt <= :endDate")
    Page<Transaction> findByBankIdAndEndDate(@Param("bankId") UUID bankId, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    @Query("SELECT t FROM Transaction t " +
            "WHERE (t.senderBank.linkedBankId = :bankId OR t.receiverBank.linkedBankId = :bankId) " +
            "AND t.createdAt >= :startDate AND t.createdAt <= :endDate")
    Page<Transaction> findByBankIdAndDateRange(
            @Param("bankId") UUID bankId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);
}
