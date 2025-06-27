package com.wnc.internet_banking.controller;

import com.wnc.internet_banking.dto.request.user.EmployeeRegistrationDto;
import com.wnc.internet_banking.dto.response.user.UserDto;
import com.wnc.internet_banking.entity.User;
import com.wnc.internet_banking.dto.response.BaseResponse;
import com.wnc.internet_banking.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
@RequestMapping("/api/employees")
@Tag(name = "Employee API", description = "Quản lý nhân viên")
public class EmployeeController {

    private final UserService userService;

    @Operation(
            summary = "Lấy danh sách nhân viên",
            description = "Trả về danh sách tất cả nhân viên với phân trang. Yêu cầu vai trò ROLE_ADMIN."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lấy danh sách nhân viên thành công"
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
                    description = "Không có quyền truy cập, yêu cầu vai trò ROLE_ADMIN",
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<Page<UserDto>>> getAllEmployees(
            @RequestParam(defaultValue = "0")
            @Parameter(description = "Số trang (mặc định 0)", example = "0") int page,
            @RequestParam(defaultValue = "10")
            @Parameter(description = "Kích thước trang (mặc định 10)", example = "10") int size) {
        Page<UserDto> employees = userService.getAllEmployees(page, size);
        return ResponseEntity.ok(BaseResponse.data(employees));
    }

    @Operation(
            summary = "Đăng ký nhân viên mới",
            description = "Đăng ký một nhân viên mới. Yêu cầu vai trò ROLE_ADMIN."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Đăng ký nhân viên thành công",
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
                    description = "Không có quyền truy cập, yêu cầu vai trò ROLE_ADMIN",
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<User>> registerEmployee(
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Thông tin đăng ký nhân viên",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EmployeeRegistrationDto.class)
                    )
            ) EmployeeRegistrationDto dto) {
        return ResponseEntity.ok(userService.createEmployee(dto));
    }

    @Operation(
            summary = "Cập nhật thông tin nhân viên",
            description = "Cập nhật thông tin nhân viên dựa trên userId. Yêu cầu vai trò ROLE_ADMIN."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Cập nhật nhân viên thành công",
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
                    description = "Không có quyền truy cập, yêu cầu vai trò ROLE_ADMIN",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Không tìm thấy nhân viên",
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<User>> updateEmployee(
            @PathVariable
            @Parameter(description = "ID của nhân viên (UUID)", required = true, example = "123e4567-e89b-12d3-a456-426614174000") UUID userId,
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Thông tin cập nhật nhân viên",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EmployeeRegistrationDto.class)
                    )
            ) EmployeeRegistrationDto dto) {
        return ResponseEntity.ok(userService.updateEmployee(userId, dto));
    }

    @Operation(
            summary = "Xóa nhân viên",
            description = "Xóa nhân viên dựa trên userId. Yêu cầu vai trò ROLE_ADMIN."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Xóa nhân viên thành công",
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
                    description = "Không có quyền truy cập, yêu cầu vai trò ROLE_ADMIN",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Không tìm thấy nhân viên",
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<Boolean>> deleteEmployee(
            @PathVariable
            @Parameter(description = "ID của nhân viên (UUID)", required = true, example = "123e4567-e89b-12d3-a456-426614174000") UUID userId) {
        return ResponseEntity.ok(userService.deleteEmployee(userId));
    }
}