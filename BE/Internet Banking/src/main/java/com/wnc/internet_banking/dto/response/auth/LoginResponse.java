package com.wnc.internet_banking.dto.response.auth;

import com.wnc.internet_banking.dto.response.user.UserDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Phản hồi sau khi đăng nhập thành công")
public class LoginResponse {

    @Schema(description = "Thông tin token gồm access token và refresh token")
    private TokenResponse token;

    @Schema(description = "Thông tin người dùng sau khi đăng nhập")
    private UserDto user;
}
