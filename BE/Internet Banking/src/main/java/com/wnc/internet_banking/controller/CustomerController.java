package com.wnc.internet_banking.controller;

import com.wnc.internet_banking.dto.request.account.DepositRequestDto;
import com.wnc.internet_banking.dto.request.user.CustomerRegistrationDto;
import com.wnc.internet_banking.dto.response.BaseResponse;
import com.wnc.internet_banking.entity.Account;
import com.wnc.internet_banking.entity.User;
import com.wnc.internet_banking.service.AccountService;
import com.wnc.internet_banking.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final UserService userService;
    private final AccountService accountService;

    public CustomerController(UserService userService, AccountService accountService) {
        this.userService = userService;
        this.accountService = accountService;
    }

    @PostMapping("/register")
    public ResponseEntity<BaseResponse<User>> registerCustomer(@RequestBody CustomerRegistrationDto dto) {
        User customer = userService.createCustomer(dto);
        return ResponseEntity.ok(BaseResponse.data(customer));
    }

    @PostMapping("/deposit")
    public ResponseEntity<BaseResponse<Account>> deposit(@RequestBody DepositRequestDto dto) {
        Account updatedAccount = accountService.deposit(dto.getAccountNumber(), dto.getAmount());
        return ResponseEntity.ok(BaseResponse.data(updatedAccount));
    }
}