package com.wnc.internet_banking.dto.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Yêu cầu đăng nhập - chứa thông tin xác thực người dùng")
public class LoginRequest {

    @Schema(description = "Tên đăng nhập của người dùng")
    private String username;

    @Schema(description = "Mật khẩu của người dùng")
    private String password;

    @Schema(description = "Mã xác thực từ Google reCAPTCHA")
    private String recaptchaToken;
}
