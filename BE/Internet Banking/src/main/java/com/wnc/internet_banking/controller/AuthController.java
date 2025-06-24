package com.wnc.internet_banking.controller;

import com.wnc.internet_banking.dto.request.auth.*;
import com.wnc.internet_banking.dto.response.BaseResponse;
import com.wnc.internet_banking.dto.response.auth.LoginResponse;
import com.wnc.internet_banking.dto.response.auth.TokenResponse;
import com.wnc.internet_banking.service.AuthService;
import com.wnc.internet_banking.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Đăng nhập", description = "Xác thực người dùng và trả về access token cùng thông tin người dùng")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Đăng nhập thành công"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Thông tin đăng nhập không hợp lệ",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class)
                    )
            ),
    })
    public ResponseEntity<BaseResponse<LoginResponse>> loginUser(@RequestBody LoginRequest request) {
        LoginResponse response = authService.loginUser(
                request.getUsername(),
                request.getPassword(),
                request.getRecaptchaToken()
        );
        return ResponseEntity.ok(BaseResponse.data(response));
    }

    @PostMapping("/logout")
    @Operation(summary = "Đăng xuất", description = "Đăng xuất người dùng hiện tại và xóa thông tin trong session")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Đăng xuất thành công"),
            @ApiResponse(
                    responseCode = "401",
                    description = "Chưa đăng nhập hoặc token không hợp lệ",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Không có quyền truy cập",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class)
                    )
            ),
    })
    public ResponseEntity<BaseResponse<Void>> logoutUser() {
        UUID userId = SecurityUtil.getCurrentUserId();
        authService.logoutUser(userId);
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(BaseResponse.message("Logged out successfully"));
    }

    @PostMapping("/change-password")
    @Operation(summary = "Đổi mật khẩu", description = "Người dùng đổi mật khẩu khi đã đăng nhập")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Đổi mật khẩu thành công"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Tham số không hợp lệ",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class)
                    )
            ),
    })
    public ResponseEntity<BaseResponse<Void>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        UUID userId = SecurityUtil.getCurrentUserId();
        authService.changePassword(userId, request.getOldPassword(), request.getNewPassword());
        return ResponseEntity.ok(BaseResponse.message("Password changed successfully"));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Làm mới token", description = "Cấp mới access token bằng refresh token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Đổi mật khẩu thành công"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Tham số không hợp lệ",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class)
                    )
            ),
    })
    public ResponseEntity<BaseResponse<TokenResponse>> refreshToken(@RequestBody RefreshTokenRequest request) {
        TokenResponse response = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(BaseResponse.data(response));
    }

    @Operation(summary = "Yêu cầu đặt lại mật khẩu", description = "Gửi email chứa mã OTP để đặt lại mật khẩu")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Gửi email thành công"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Tham số không hợp lệ",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class)
                    )
            ),
    })
    @PostMapping("/password-reset/initiate")
    public ResponseEntity<BaseResponse<UUID>> initiateInternalTransfer (
            @RequestBody PasswordResetRequest passwordResetRequest
    ) {
        UUID userId = authService.initiatePasswordReset(passwordResetRequest.getEmail());
        return ResponseEntity.ok(BaseResponse.data(userId));
    }

    @Operation(summary = "Xác minh đặt lại mật khẩu", description = "Xác thực mã OTP và cập nhật mật khẩu mới")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cập nhật mật khẩu thành công"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Tham số không hợp lệ",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class)
                    )
            ),
    })
    @PostMapping("/password-reset/verify")
    public ResponseEntity<BaseResponse<Void>> verifyPasswordReset(@RequestBody PasswordResetConfirmRequest request) {
        authService.verifyPasswordReset(UUID.fromString(request.getUserId()), request.getOtp(), request.getNewPassword());
        return ResponseEntity.ok(BaseResponse.message("Reset password successfully"));
    }
}
