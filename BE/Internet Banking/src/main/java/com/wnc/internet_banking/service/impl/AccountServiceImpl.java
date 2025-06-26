package com.wnc.internet_banking.service.impl;

import com.wnc.internet_banking.dto.response.BaseResponse;
import com.wnc.internet_banking.dto.response.account.AccountDto;
import com.wnc.internet_banking.dto.response.linkedbank.AccountResponseDto;
import com.wnc.internet_banking.entity.Account;
import com.wnc.internet_banking.entity.User;
import com.wnc.internet_banking.repository.AccountRepository;
import com.wnc.internet_banking.repository.UserRepository;
import com.wnc.internet_banking.service.AccountService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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
    private final UserRepository userRepository;

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
    public String getAccountNumberByUserId(UUID userId) {
        Optional<Account> accountOptional = accountRepository.findByUser_UserId(userId);
        if (accountOptional.isPresent()) {
            return accountOptional.get().getAccountNumber();
        }
        throw new EntityNotFoundException("Account not found for user ID: " + userId);
    }

    @Override
    public AccountResponseDto getAccountByAccountNumber(String accountNumber) {
        Account account= accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with account number: " + accountNumber));

        AccountResponseDto accountDto = new AccountResponseDto();
        accountDto.setAccountNumber(account.getAccountNumber());
        accountDto.setFullName(account.getUser().getFullName());
        return accountDto;
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

//    @Override
//    @Transactional
//    public Account deposit(String accountNumber, double amount) {
//        if (amount <= 0) {
//            throw new IllegalArgumentException("Deposit amount must be greater than zero.");
//        }
//
//        Account account = accountRepository.findByAccountNumber(accountNumber)
//                .orElseThrow(() -> new EntityNotFoundException("Account not found with account number: " + accountNumber));
//
//        account.setBalance(account.getBalance() + amount);
//        return accountRepository.save(account);
//    }

    @Override
    @Transactional
    public BaseResponse<Account> deposit(String accountNumberOrUsername, double amount) {
        if (amount <= 0) {
            return BaseResponse.message("Số tiền nạp phải lớn hơn 0");
        }

        // Bước 1: Tìm tài khoản bằng accountNumber
        Optional<Account> accountOpt = accountRepository.findByAccountNumber(accountNumberOrUsername);

        // Bước 2: Nếu không tìm thấy, coi accountNumberOrUsername là username và tìm tài khoản qua user
        if (accountOpt.isEmpty()) {
            Optional<User> userOpt = userRepository.findByUsername(accountNumberOrUsername);
            if (userOpt.isEmpty()) {
                return BaseResponse.message("Tài khoản hoặc Username không tồn tại");
            }
            accountOpt = accountRepository.findByUser(userOpt.get());
            if (accountOpt.isEmpty()) {
                throw new EntityNotFoundException("Người dùng này không có tài khoản");
            }
        }

        // Cập nhật số dư
        Account account = accountOpt.get();
        account.setBalance(account.getBalance() + amount);
        return BaseResponse.data(accountRepository.save(account));
    }
}
