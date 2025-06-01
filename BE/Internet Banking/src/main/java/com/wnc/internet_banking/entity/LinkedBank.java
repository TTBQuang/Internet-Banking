package com.wnc.internet_banking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "linked_banks")
public class LinkedBank {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "linked_bank_id")
    private UUID linkedBankId;

    @NotNull
    @Column(name = "bank_code", nullable = false, unique = true)
    private String bankCode;

    @NotNull
    @Column(name = "bank_name", nullable = false)
    private String bankName;

    @NotNull
    @Column(name = "public_key", nullable = false, columnDefinition = "TEXT")
    private String publicKey;

    @NotNull
    @Column(name = "secret_key_hash", nullable = false)
    private String secretKeyHash;

    @NotNull
    @Column(name = "encryption_method", nullable = false)
    @Enumerated(EnumType.STRING)
    private EncryptionMethod encryptionMethod;

    @NotNull
    @Column(name = "get_account_info_url", nullable = false)
    private String getAccountInfoUrl;

    @NotNull
    @Column(name = "money_transfer_url", nullable = false)
    private String moneyTransferUrl;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum EncryptionMethod {
        RSA, PGP
    }
}

