package com.wnc.internet_banking.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "debt_reminders")
public class DebtReminder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "debt_reminder_id", updatable = false, nullable = false)
    private UUID debtReminderId;

    @ManyToOne
    @JoinColumn(name = "creditor_id", nullable = false)
    private User creditor;

    @ManyToOne
    @JoinColumn(name = "debtor_account_number", referencedColumnName = "account_number")
    private Account debtorAccount;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    public enum Status {
        PENDING, PAID
    }
}

