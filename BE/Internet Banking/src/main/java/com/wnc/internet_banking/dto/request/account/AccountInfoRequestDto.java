package com.wnc.internet_banking.dto.request.account;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AccountInfoRequestDto {
    @Schema(description = "Số tài khoản muốn lấy dữ liệu", example = "ACC7A0C68BADB")
    private String accountNumber;
}