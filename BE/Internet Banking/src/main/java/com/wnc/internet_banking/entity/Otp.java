package com.wnc.internet_banking.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "otps")
public class Otp {
    @Id
    @Column(name = "otp_id")
    private UUID otpId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "otp_code", nullable = false)
    private String otpCode;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Purpose purpose;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    @Column(name = "is_used", nullable = false)
    private boolean isUsed = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum Purpose {
        LOGIN, TRANSACTION, PASSWORD_RESET
    }
}

