package com.wnc.internet_banking.dto.response.auth;

import lombok.Getter;

import java.util.UUID;

@Getter
public class AccountDto {
    private UUID accountId;
    private String accountNumber;
    private double balance;
}
