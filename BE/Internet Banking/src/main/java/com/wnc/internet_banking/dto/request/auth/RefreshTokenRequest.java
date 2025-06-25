package com.wnc.internet_banking.dto.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Yêu cầu cấp lại access token bằng refresh token")
public class RefreshTokenRequest {

    @Schema(description = "Refresh token hợp lệ đã được cấp trước đó")
    private String refreshToken;
}
