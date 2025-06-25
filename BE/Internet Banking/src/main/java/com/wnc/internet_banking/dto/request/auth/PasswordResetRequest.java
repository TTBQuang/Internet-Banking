package com.wnc.internet_banking.dto.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Yêu cầu gửi mã OTP để đặt lại mật khẩu")
public class PasswordResetRequest {

    @Email(message = "Email is invalid")
    @Schema(description = "Email của người dùng cần đặt lại mật khẩu")
    private String email;
}
