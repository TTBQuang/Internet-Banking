package com.wnc.internet_banking.dto.response.account;

import com.wnc.internet_banking.dto.response.user.UserDto;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AccountDto {
    private UUID accountId;
    private String accountNumber;
    private UserDto user;
    private double balance;
}
