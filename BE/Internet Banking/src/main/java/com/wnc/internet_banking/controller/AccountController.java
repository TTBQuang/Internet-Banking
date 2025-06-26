package com.wnc.internet_banking.controller;

import com.wnc.internet_banking.dto.response.BaseResponse;
import com.wnc.internet_banking.dto.response.account.AccountDto;
import com.wnc.internet_banking.dto.response.linkedbank.AccountResponseDto;
import com.wnc.internet_banking.service.AccountService;
import com.wnc.internet_banking.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/account")
@AllArgsConstructor
@Tag(name = "Account API", description = "Quản lý tài khoản")
public class AccountController {
    private final AccountService accountService;

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Lấy thông tin tài khoản người dùng hiện tại", description = "Trả về thông tin tài khoản của người dùng hiện đang đăng nhập")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy thông tin thành công"),
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
            )
    })
    public ResponseEntity<BaseResponse<AccountDto>> getCurrentUserAccount() {
        UUID userId = SecurityUtil.getCurrentUserId();
        AccountDto accountDTO = accountService.getAccountByUserId(userId);
        return ResponseEntity.ok(BaseResponse.data(accountDTO));
    }

    @Operation(summary = "Lấy số tài khoản theo ID người dùng", description = "Trả về số tài khoản tương ứng với ID người dùng được cung cấp")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy thông tin thành công"),
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
            @ApiResponse(
                    responseCode = "404",
                    description = "Không tìm thấy tài khoản cho người dùng",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class)
                    )
            ),
    })
    @GetMapping("/account-number")
    public ResponseEntity<BaseResponse<String>> getAccountNumberByUserId(@RequestParam UUID userId) {
        String accountNumber = accountService.getAccountNumberByUserId(userId);

        return ResponseEntity.ok(BaseResponse.data(accountNumber));
    }

    @Operation(summary = "Lấy thông tin tài khoản theo số tài khoản", description = "Trả về thông tin chi tiết tài khoản dựa vào số tài khoản")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy thông tin thành công"),
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
            @ApiResponse(
                    responseCode = "404",
                    description = "Không tìm thấy tài khoản",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class)
                    )
            ),
    })
    @GetMapping("/{accountNumber}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BaseResponse<?>> getAccountByAccountNumber(
            @PathVariable String accountNumber) {
        AccountResponseDto account = accountService.getAccountByAccountNumber(accountNumber);
        return ResponseEntity.ok(BaseResponse.data(account));
    }
}
