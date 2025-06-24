package com.wnc.internet_banking.dto.request.recipient;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Yêu cầu cập nhật tên gợi nhớ của người thụ hưởng")
public class RecipientUpdateRequest {

    @NotBlank(message = "Nickname is required")
    @Schema(description = "Tên gợi nhớ mới cho người thụ hưởng")
    private String nickname;
}
