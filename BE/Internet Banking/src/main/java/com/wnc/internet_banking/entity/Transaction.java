package com.wnc.internet_banking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "transaction_id")
    private UUID transactionId;

    @NotNull
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

    @NotNull
    @Column(name = "amount", nullable = false)
    private Double amount;

    @NotNull
    @Column(name = "fee", nullable = false)
    private Double fee;

    @Enumerated(EnumType.STRING)
    @Column(name = "fee_payer", nullable = false)
    private FeePayer feePayer;

    @NotNull
    @Column(name = "content", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private Type type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @ColumnDefault("'PENDING'")
    private Status status = Status.PENDING;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    public enum FeePayer {
        SENDER, RECEIVER
    }

    public enum Type {
        MONEY_TRANSFER, DEBT_PAYMENT
    }

    public enum Status {
        PENDING, COMPLETED, FAILED
    }
}

