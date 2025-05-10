package com.wnc.internet_banking.service.impl;

import com.wnc.internet_banking.dto.response.auth.AccountDto;
import com.wnc.internet_banking.entity.Account;
import com.wnc.internet_banking.entity.User;
import com.wnc.internet_banking.repository.AccountRepository;
import com.wnc.internet_banking.service.AccountService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service("accountService")
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;

    @Override
    public AccountDto getAccountByUserId(UUID userId) {
        Optional<Account> accountOptional = accountRepository.findByUser_UserId(userId);
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            return modelMapper.map(account, AccountDto.class);
        }
        throw new EntityNotFoundException("Account not found for user ID: " + userId);
    }

    @Override
    @Transactional
    public Account createAccountForUser(User user) {
        Account account = new Account();
        account.setAccountNumber(generateAccountNumber());
        account.setBalance(0.0);
        account.setUser(user);
        return accountRepository.save(account);
    }

    private String generateAccountNumber() {
        return "ACC" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
    }

    @Override
    @Transactional
    public Account deposit(String accountNumber, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than zero.");
        }

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new EntityNotFoundException("Account not found with account number: " + accountNumber));

        account.setBalance(account.getBalance() + amount);
        return accountRepository.save(account);
    }
}
