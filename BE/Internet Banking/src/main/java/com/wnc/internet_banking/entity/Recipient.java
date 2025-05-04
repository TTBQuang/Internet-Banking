package com.wnc.internet_banking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "recipients")
public class Recipient {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "recipient_id")
    private UUID recipientId;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "account_number", nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private String nickname;

    @ManyToOne
    @JoinColumn(name = "bank_code", referencedColumnName = "bank_code")
    private LinkedBank bank;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}

