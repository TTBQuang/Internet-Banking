package com.wnc.internet_banking.dto.request.recipient;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
@Schema(description = "Yêu cầu tạo người thụ hưởng mới")
public class RecipientCreateRequest {

    @NotBlank(message = "Recipient account number is required")
    @Schema(description = "Số tài khoản của người thụ hưởng")
    private String accountNumber;

    @Schema(description = "Tên gợi nhớ do người dùng đặt cho thụ hưởng")
    private String nickname;

    @Schema(description = "ID của ngân hàng liên kết")
    private UUID bankId;

    @Schema(description = "Họ tên của người thụ hưởng (không được null nếu là ngân hàng ngoài)")
    private String fullName; // Not null when bankId is not null
}
