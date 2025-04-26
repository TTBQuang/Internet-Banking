package com.wnc.internet_banking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "recipients")
@Setter
@Getter
public class Recipient {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "recipient_id", updatable = false, nullable = false)
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

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}

