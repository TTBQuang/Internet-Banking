package com.wnc.internet_banking.service;

import com.wnc.internet_banking.dto.response.auth.AccountDto;
import com.wnc.internet_banking.entity.Account;
import com.wnc.internet_banking.entity.User;

import java.util.UUID;

public interface AccountService {
    AccountDto getAccountByUserId(UUID userId);

    Account createAccountForUser(User user);

    Account deposit(String accountNumber, double amount);
}
