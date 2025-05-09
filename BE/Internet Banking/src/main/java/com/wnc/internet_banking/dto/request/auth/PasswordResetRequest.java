package com.wnc.internet_banking.dto.request.auth;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PasswordResetRequest {
    @Email(message = "Email is invalid")
    private String email;
}
