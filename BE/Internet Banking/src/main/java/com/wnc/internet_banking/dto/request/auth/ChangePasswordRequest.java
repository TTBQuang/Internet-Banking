package com.wnc.internet_banking.dto.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Yêu cầu đổi mật khẩu của người dùng")
public class ChangePasswordRequest {

    @NotBlank(message = "Old password is required")
    @Schema(description = "Mật khẩu cũ của người dùng")
    private String oldPassword;

    @NotBlank(message = "New password is required")
    @Schema(description = "Mật khẩu mới mà người dùng muốn đặt")
    private String newPassword;
}