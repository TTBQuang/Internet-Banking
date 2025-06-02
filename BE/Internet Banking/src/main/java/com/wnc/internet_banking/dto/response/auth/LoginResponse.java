package com.wnc.internet_banking.dto.response.auth;

import com.wnc.internet_banking.dto.response.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private TokenResponse token;
    private UserDto user;
}
