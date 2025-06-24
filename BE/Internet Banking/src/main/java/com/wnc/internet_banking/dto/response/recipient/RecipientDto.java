package com.wnc.internet_banking.dto.response.recipient;

import com.wnc.internet_banking.dto.response.linkedbank.LinkedBankDto;
import com.wnc.internet_banking.dto.response.user.UserDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Thông tin người thụ hưởng")
public class RecipientDto {

    @Schema(description = "ID của người thụ hưởng")
    private UUID recipientId;

    @Schema(description = "Người sở hữu (người dùng đã lưu thụ hưởng này)")
    private UserDto owner;

    @Schema(description = "Số tài khoản của người thụ hưởng")
    private String accountNumber;

    @Schema(description = "Tên gợi nhớ (nickname) do người dùng đặt")
    private String nickname;

    @Schema(description = "Ngân hàng liên kết với tài khoản thụ hưởng")
    private LinkedBankDto bank;

    @Schema(description = "Thời điểm tạo người thụ hưởng")
    private LocalDateTime createdAt;
}
