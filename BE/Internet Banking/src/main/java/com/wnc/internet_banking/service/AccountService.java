package com.wnc.internet_banking.service;

import com.wnc.internet_banking.dto.response.auth.AccountDto;
import com.wnc.internet_banking.entity.Account;
import com.wnc.internet_banking.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;

    public AccountDto getAccountByUserId(UUID userId) {
        Optional<Account> accountOptional = accountRepository.findByUser_UserId(userId);
        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            return modelMapper.map(account, AccountDto.class);
        }
        throw new EntityNotFoundException("Account not found for user ID: " + userId);
    }
}
