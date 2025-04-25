package com.wnc.internet_banking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "recipents")
@Setter
@Getter
public class Recipient {
    @Id
    @Column(name = "recipient_id")
    private UUID recipientId;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "account_number", nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private String nickname = "user.username"; // Mặc định

    @ManyToOne
    @JoinColumn(name = "bank_code", referencedColumnName = "bank_code")
    private LinkedBank bank;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}

