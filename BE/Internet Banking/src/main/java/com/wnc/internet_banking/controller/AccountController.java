package com.wnc.internet_banking.controller;

import com.wnc.internet_banking.dto.response.BaseResponse;
import com.wnc.internet_banking.dto.response.account.AccountDto;
import com.wnc.internet_banking.service.AccountService;
import com.wnc.internet_banking.util.SecurityUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/account")
@AllArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BaseResponse<AccountDto>> getAccountByUserId() {
        UUID userId = SecurityUtil.getCurrentUserId();
        AccountDto accountDTO = accountService.getAccountByUserId(userId);
        return ResponseEntity.ok(BaseResponse.data(accountDTO));
    }
}
