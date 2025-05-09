package com.wnc.internet_banking.dto.request.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PasswordResetConfirmRequest {
    private String userId;
    private String otp;
    private String newPassword;
}
