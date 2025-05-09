package com.wnc.internet_banking.controller;

import com.wnc.internet_banking.dto.request.auth.*;
import com.wnc.internet_banking.dto.request.transaction.InternalTransferRequest;
import com.wnc.internet_banking.dto.response.BaseResponse;
import com.wnc.internet_banking.dto.response.auth.LoginResponse;
import com.wnc.internet_banking.dto.response.auth.TokenResponse;
import com.wnc.internet_banking.service.AuthService;
import com.wnc.internet_banking.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
        LoginResponse response = authService.loginUser(
                request.getUsername(),
                request.getPassword(),
                request.getRecaptchaToken()
        );
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

    @PostMapping("/refresh")
    public ResponseEntity<BaseResponse<TokenResponse>> refreshToken(@RequestBody RefreshTokenRequest request) {
        TokenResponse response = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(BaseResponse.data(response));
    }

    @PostMapping("/password-reset/initiate")
    public ResponseEntity<BaseResponse<UUID>> initiateInternalTransfer (
            @RequestBody PasswordResetRequest passwordResetRequest
    ) {
        UUID userId = authService.initiatePasswordReset(passwordResetRequest.getEmail());
        return ResponseEntity.ok(BaseResponse.data(userId));
    }

    @PostMapping("/password-reset/verify")
    public ResponseEntity<BaseResponse<Void>> verifyPasswordReset(@RequestBody PasswordResetConfirmRequest request) {
        authService.verifyPasswordReset(UUID.fromString(request.getUserId()), request.getOtp(), request.getNewPassword());
        return ResponseEntity.ok(BaseResponse.message("Reset password successfully"));
    }
}
