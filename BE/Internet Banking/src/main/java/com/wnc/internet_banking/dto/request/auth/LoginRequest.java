package com.wnc.internet_banking.dto.request.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginRequest {
    private String username;
    private String password;
    private String recaptchaToken;
}
