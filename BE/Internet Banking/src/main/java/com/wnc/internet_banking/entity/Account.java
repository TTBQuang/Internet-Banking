package com.wnc.internet_banking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "account_id")
    private UUID accountId;

    @NotNull
    @Column(name = "account_number", nullable = false, unique = true)
    private String accountNumber;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @NotNull
    @Column(name = "balance", nullable = false)
    private Double balance;
}