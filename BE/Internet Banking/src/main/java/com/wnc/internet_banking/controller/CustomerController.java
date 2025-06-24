package com.wnc.internet_banking.controller;

import com.wnc.internet_banking.dto.request.account.DepositRequestDto;
import com.wnc.internet_banking.dto.request.user.CustomerRegistrationDto;
import com.wnc.internet_banking.dto.response.BaseResponse;
import com.wnc.internet_banking.dto.response.user.UserDto;
import com.wnc.internet_banking.entity.Account;
import com.wnc.internet_banking.entity.User;
import com.wnc.internet_banking.service.AccountService;
import com.wnc.internet_banking.service.UserService;
import com.wnc.internet_banking.service.impl.AccountServiceImpl;
import com.wnc.internet_banking.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final UserService userService;
    private final AccountService accountService;


    @GetMapping
    public ResponseEntity<BaseResponse<Page<User>>> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(BaseResponse.data(userService.getAllCustomers(page, size)));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<BaseResponse<User>> updateCustomer(
            @PathVariable UUID userId,
            @RequestBody CustomerRegistrationDto request) {
        return ResponseEntity.ok(BaseResponse.data(userService.updateCustomer(userId, request)));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable UUID userId) {
        userService.deleteCustomer(userId);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/register")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<BaseResponse<User>> registerCustomer(@RequestBody CustomerRegistrationDto dto) {
        User customer = userService.createCustomer(dto);
        return ResponseEntity.ok(BaseResponse.data(customer));
    }

    @PostMapping("/deposit")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<BaseResponse<Account>> deposit(@RequestBody DepositRequestDto dto) {
        Account updatedAccount = accountService.deposit(dto.getAccountNumber(), dto.getAmount());
        return ResponseEntity.ok(BaseResponse.data(updatedAccount));
    }
}