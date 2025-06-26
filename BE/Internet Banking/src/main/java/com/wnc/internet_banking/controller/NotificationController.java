package com.wnc.internet_banking.controller;

import com.wnc.internet_banking.dto.response.BaseResponse;
import com.wnc.internet_banking.dto.response.notification.NotificationDto;
import com.wnc.internet_banking.service.NotificationService;
import com.wnc.internet_banking.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification API", description = "Quản lý thông báo cho người dùng")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "Lấy danh sách thông báo", description = "Trả về tất cả thông báo của người dùng hiện tại")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lấy danh sách thông báo thành công"),
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
    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BaseResponse<List<NotificationDto>>> getAllNotifications() {
        UUID userId = SecurityUtil.getCurrentUserId();
        List<NotificationDto> notifications = notificationService.getAllNotificationsByUserId(userId);
        return ResponseEntity.ok(BaseResponse.data(notifications));
    }

    @Operation(summary = "Đánh dấu đã đọc 1 thông báo", description = "Đánh dấu một thông báo cụ thể là đã đọc")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Đánh dấu thành công"),
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
                    description = "Không tìm thấy thông báo",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class)
                    )
            ),
    })
    @PutMapping("/{id}/read")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BaseResponse<Void>> markAsRead(
            @PathVariable
            @Parameter(description = "ID của thông báo (UUID)", required = true)
            UUID id) {
        UUID userId = SecurityUtil.getCurrentUserId();
        notificationService.markAsRead(userId, id);
        return ResponseEntity.ok(BaseResponse.message("Marked as read"));
    }

    @Operation(summary = "Đánh dấu tất cả đã đọc", description = "Đánh dấu tất cả thông báo của người dùng là đã đọc")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Đánh dấu tất cả thành công"),
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
    @PutMapping("/read-all")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BaseResponse<Void>> markAllAsRead() {
        UUID userId = SecurityUtil.getCurrentUserId();
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(BaseResponse.message("Marked as read"));
    }
}
