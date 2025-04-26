package com.wnc.internet_banking.controller;

import com.wnc.internet_banking.dto.request.auth.ChangePasswordRequest;
import com.wnc.internet_banking.dto.request.auth.LoginRequest;
import com.wnc.internet_banking.dto.response.BaseResponse;
import com.wnc.internet_banking.dto.response.auth.LoginResponse;
import com.wnc.internet_banking.service.AuthService;
import com.wnc.internet_banking.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<LoginResponse>> loginUser(@RequestBody LoginRequest request) {
        LoginResponse response = authService.loginUser(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(BaseResponse.data(response));
    }

    @PostMapping("/logout")
    public ResponseEntity<BaseResponse<Void>> logoutUser() {
        UUID userId = SecurityUtil.getCurrentUserId();
        authService.logoutUser(userId);
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(BaseResponse.message("Logged out successfully"));
    }

    @PostMapping("/change-password")
    public ResponseEntity<BaseResponse<Void>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        UUID userId = SecurityUtil.getCurrentUserId();
        authService.changePassword(userId, request.getOldPassword(), request.getNewPassword());
        return ResponseEntity.ok(BaseResponse.message("Password changed successfully"));
    }
}
