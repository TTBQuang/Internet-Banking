package com.wnc.internet_banking.dto.response.account;

import com.wnc.internet_banking.dto.response.user.UserDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AccountDto {
    @Schema(description = "ID tài khoản")
    private UUID accountId;

    @Schema(description = "Số tài khoản")
    private String accountNumber;

    @Schema(description = "Thông tin người dùng liên kết")
    private UserDto user;

    @Schema(description = "Số dư tài khoản")
    private double balance;
}
