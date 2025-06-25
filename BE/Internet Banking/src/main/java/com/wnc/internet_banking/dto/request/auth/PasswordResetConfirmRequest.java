package com.wnc.internet_banking.dto.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Yêu cầu xác nhận đặt lại mật khẩu với mã OTP")
public class PasswordResetConfirmRequest {

    @Schema(description = "ID của người dùng được gửi kèm trong email")
    private String userId;

    @Schema(description = "Mã OTP được gửi đến email của người dùng")
    private String otp;

    @Schema(description = "Mật khẩu mới mà người dùng muốn đặt")
    private String newPassword;
}
