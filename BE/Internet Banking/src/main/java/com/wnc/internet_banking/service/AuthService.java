package com.wnc.internet_banking.service;

import com.wnc.internet_banking.dto.response.auth.LoginResponse;
import com.wnc.internet_banking.dto.response.auth.TokenResponse;

import java.util.UUID;

public interface AuthService {
    LoginResponse loginUser(String username, String rawPassword, String recaptchaToken);
    void logoutUser(UUID userId);
    void changePassword(UUID userId, String oldPassword, String newPassword);
    TokenResponse refreshToken(String refreshToken);
    UUID initiatePasswordReset(String email);
    void verifyPasswordReset(UUID userId, String otpCode, String newPassword);
}
