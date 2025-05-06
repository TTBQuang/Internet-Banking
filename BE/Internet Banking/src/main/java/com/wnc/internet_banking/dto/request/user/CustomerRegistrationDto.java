package com.wnc.internet_banking.dto.request.user;

import lombok.Data;

@Data
public class CustomerRegistrationDto {
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phone;
}