package com.wnc.internet_banking.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "linked_banks")
public class LinkedBank {
    @Id
    @Column(name = "linked_bank_id")
    private UUID linkedBankId;

    @Column(name = "bank_code", nullable = false, unique = true)
    private String bankCode;

    @Column(name = "bank_name", nullable = false)
    private String bankName;

    @Column(name = "public_key", nullable = false)
    private String publicKey;

    @Column(name = "encryption_method", nullable = false)
    @Enumerated(EnumType.STRING)
    private EncryptionMethod encryptionMethod;

    @Column(name = "get_account_info_url", nullable = false)
    private String getAccountInfoUrl;

    @Column(name = "money_transfer_url", nullable = false)
    private String moneyTransferUrl;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum EncryptionMethod {
        RSA, PGP
    }
}

