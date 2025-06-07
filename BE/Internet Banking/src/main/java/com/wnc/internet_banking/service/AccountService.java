package com.wnc.internet_banking.service;

import com.wnc.internet_banking.dto.response.account.AccountDto;
import com.wnc.internet_banking.dto.response.linkedbank.AccountResponseDto;
import com.wnc.internet_banking.entity.Account;
import com.wnc.internet_banking.entity.User;

import java.util.UUID;

public interface AccountService {
    AccountDto getAccountByUserId(UUID userId);

    String getAccountNumberByUserId(UUID userId);

    AccountResponseDto getAccountByAccountNumber(String accountNumber);

    Account createAccountForUser(User user);

    Account deposit(String accountNumber, double amount);
}
