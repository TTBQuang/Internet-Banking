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
@Schema(description = "Thông tin người nhận")
public class RecipientDto {

    @Schema(description = "ID của người nhận")
    private UUID recipientId;

    @Schema(description = "Người sở hữu (người dùng đã lưu người nhận này)")
    private UserDto owner;

    @Schema(description = "Số tài khoản của người nhận")
    private String accountNumber;

    @Schema(description = "Tên gợi nhớ (nickname) do người dùng đặt")
    private String nickname;

    @Schema(description = "Ngân hàng liên kết với tài khoản nhận")
    private LinkedBankDto bank;

    @Schema(description = "Thời điểm tạo người nhận")
    private LocalDateTime createdAt;
}
