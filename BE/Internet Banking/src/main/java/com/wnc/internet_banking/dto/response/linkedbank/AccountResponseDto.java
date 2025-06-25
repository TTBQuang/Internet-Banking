package com.wnc.internet_banking.dto.response.linkedbank;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Thông tin tài khoản người dùng")
public class AccountResponseDto {
    @Schema(description = "Số tài khoản", example = "5873906278933357")
    private String accountNumber;
    @Schema(description = "Họ và tên", example = "Nguyễn Thị A")
    private String fullName;
}
