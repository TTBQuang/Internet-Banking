package com.wnc.internet_banking.controller;

import com.wnc.internet_banking.dto.response.auth.AccountDto;
import com.wnc.internet_banking.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/account")
@AllArgsConstructor
public class AccountController {
    private AccountService accountService;

    @GetMapping()
    public ResponseEntity<AccountDto> getAccountByUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            UUID userId = UUID.fromString(authentication.getName());
            AccountDto accountDTO = accountService.getAccountByUserId(userId);
            return ResponseEntity.ok(accountDTO);
        } else {
            throw new AccessDeniedException("Unauthorized");
        }
    }
}
