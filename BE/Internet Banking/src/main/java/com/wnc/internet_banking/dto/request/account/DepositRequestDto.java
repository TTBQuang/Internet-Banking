package com.wnc.internet_banking.dto.request.account;

import lombok.Data;

@Data
public class DepositRequestDto {
    private String accountNumber;
    private double amount;
}