package com.wnc.internet_banking.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "transaction_id", updatable = false, nullable = false)
    private UUID transactionId;

    @Column(name = "sender_account_number", nullable = false)
    private String senderAccountNumber;

    @ManyToOne
    @JoinColumn(name = "sender_bank_code", referencedColumnName = "bank_code", nullable = false)
    private LinkedBank senderBank;

    @Column(name = "receiver_account_number")
    private String receiverAccountNumber;

    @ManyToOne
    @JoinColumn(name = "receiver_bank_code", referencedColumnName = "bank_code")
    private LinkedBank receiverBank;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false)
    private double fee;

    @Enumerated(EnumType.STRING)
    @Column(name = "fee_payer", nullable = false)
    private FeePayer feePayer;

    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "confirmed_at", nullable = false)
    private LocalDateTime confirmedAt;

    public enum FeePayer {
        SENDER, RECEIVE
    }

    public enum Type {
        MONEY_TRANSFER, DEBT_PAYMENT
    }

    public enum Status {
        PENDING, COMPLETED, FAILED
    }
}

