package com.wnc.internet_banking.dto.response.user;

import com.wnc.internet_banking.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {
    private UUID userId;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private User.Role role;
}
