package com.wnc.internet_banking.controller;

import com.wnc.internet_banking.dto.request.account.DepositRequestDto;
import com.wnc.internet_banking.dto.request.user.CustomerRegistrationDto;
import com.wnc.internet_banking.dto.response.BaseResponse;
import com.wnc.internet_banking.dto.response.user.UserDto;
import com.wnc.internet_banking.entity.Account;
import com.wnc.internet_banking.entity.User;
import com.wnc.internet_banking.service.AccountService;
import com.wnc.internet_banking.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/customers")
@Tag(name = "Customer API", description = "Quản lý khách hàng")
public class CustomerController {

    private final UserService userService;
    private final AccountService accountService;

    @Operation(
            summary = "Lấy danh sách khách hàng",
            description = "Trả về danh sách tất cả khách hàng với phân trang. Yêu cầu vai trò ROLE_EMPLOYEE."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lấy danh sách khách hàng thành công"
            ),
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
                    description = "Không có quyền truy cập, yêu cầu vai trò ROLE_EMPLOYEE",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Lỗi hệ thống",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class)
                    )
            )
    })
    @GetMapping
    public ResponseEntity<BaseResponse<Page<UserDto>>> getAllCustomers(
            @RequestParam(defaultValue = "0")
            @Parameter(description = "Số trang (mặc định 0)", example = "0") int page,
            @RequestParam(defaultValue = "10")
            @Parameter(description = "Kích thước trang (mặc định 10)", example = "10") int size) {
        return ResponseEntity.ok(BaseResponse.data(userService.getAllCustomers(page, size)));
    }

    @Operation(
            summary = "Cập nhật thông tin khách hàng",
            description = "Cập nhật thông tin khách hàng dựa trên userId. Yêu cầu vai trò ROLE_EMPLOYEE."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Cập nhật khách hàng thành công",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class)
                    )
            ),
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
                    description = "Không có quyền truy cập, yêu cầu vai trò ROLE_EMPLOYEE",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Không tìm thấy khách hàng",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dữ liệu đầu vào không hợp lệ",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Lỗi hệ thống",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class)
                    )
            )
    })
    @PutMapping("/{userId}")
    public ResponseEntity<BaseResponse<User>> updateCustomer(
            @PathVariable
            @Parameter(description = "ID của khách hàng (UUID)", required = true, example = "123e4567-e89b-12d3-a456-426614174000") UUID userId,
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Thông tin cập nhật khách hàng",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomerRegistrationDto.class)
                    )
            ) CustomerRegistrationDto request) {
        return ResponseEntity.ok(BaseResponse.data(userService.updateCustomer(userId, request)));
    }

    @Operation(
            summary = "Xóa khách hàng",
            description = "Xóa khách hàng dựa trên userId. Yêu cầu vai trò ROLE_EMPLOYEE."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Xóa khách hàng thành công"
            ),
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
                    description = "Không có quyền truy cập, yêu cầu vai trò ROLE_EMPLOYEE",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Không tìm thấy khách hàng",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Lỗi hệ thống",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class)
                    )
            )
    })
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteCustomer(
            @PathVariable
            @Parameter(description = "ID của khách hàng (UUID)", required = true, example = "123e4567-e89b-12d3-a456-426614174000") UUID userId) {
        userService.deleteCustomer(userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Đăng ký khách hàng mới",
            description = "Đăng ký một khách hàng mới. Yêu cầu vai trò ROLE_EMPLOYEE."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Đăng ký khách hàng thành công",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class)
                    )
            ),
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
                    description = "Không có quyền truy cập, yêu cầu vai trò ROLE_EMPLOYEE",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dữ liệu đầu vào không hợp lệ",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Lỗi hệ thống",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class)
                    )
            )
    })
    @PostMapping("/register")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<BaseResponse<User>> registerCustomer(
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Thông tin đăng ký khách hàng",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CustomerRegistrationDto.class)
                    )
            ) CustomerRegistrationDto dto) {
        return ResponseEntity.ok(userService.createCustomer(dto));
    }

    @Operation(
            summary = "Nạp tiền vào tài khoản",
            description = "Nạp tiền vào tài khoản khách hàng. Yêu cầu vai trò ROLE_EMPLOYEE."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Nạp tiền thành công",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class)
                    )
            ),
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
                    description = "Không có quyền truy cập, yêu cầu vai trò ROLE_EMPLOYEE",
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
            @ApiResponse(
                    responseCode = "400",
                    description = "Dữ liệu đầu vào không hợp lệ",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Lỗi hệ thống",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class)
                    )
            )
    })
    @PostMapping("/deposit")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<BaseResponse<Account>> deposit(
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Thông tin nạp tiền",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DepositRequestDto.class)
                    )
            ) DepositRequestDto dto) {
        return ResponseEntity.ok(accountService.deposit(dto.getAccountNumber(), dto.getAmount()));
    }
}