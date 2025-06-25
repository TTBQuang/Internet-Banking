package com.wnc.internet_banking.dto.response.linkedbank;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Thông tin ngân hàng liên kết")
public class LinkedBankDto {
    @Schema(description = "UUID ngân hàng", example = "1e3d1a9a-9f85-4e1a-9a8f-123456789abc")
    private UUID linkedBankId;
    @Schema(description = "Mã ngân hàng", example = "FIN")
    private String bankCode;
    @Schema(description = "Tên ngân hàng", example = "Banking HCMUS N3")
    private String bankName;
}
