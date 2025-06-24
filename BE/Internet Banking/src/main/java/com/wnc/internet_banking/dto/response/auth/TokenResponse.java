package com.wnc.internet_banking.dto.response.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Token truy cập gồm access token và refresh token")
public record TokenResponse(

        @Schema(description = "Access token dùng để xác thực các API")
        String accessToken,

        @Schema(description = "Refresh token dùng để cấp lại access token mới khi hết hạn")
        String refreshToken

) {}
