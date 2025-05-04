package com.wnc.internet_banking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "user_id")
    private UUID userId;

    @NotNull
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @NotNull
    @Column(name = "password", nullable = false)
    private String password;

    @NotNull
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @NotNull
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotNull
    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'CUSTOMER'")
    private Role role = Role.CUSTOMER;

    @NotNull
    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;

    public enum Role {
        CUSTOMER, EMPLOYEE, ADMIN
    }
}