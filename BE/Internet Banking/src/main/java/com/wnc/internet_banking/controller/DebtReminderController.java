package com.wnc.internet_banking.controller;

import com.wnc.internet_banking.dto.request.debtreminder.CancelDebtReminderRequest;
import com.wnc.internet_banking.dto.request.debtreminder.CreateDebtReminderRequest;
import com.wnc.internet_banking.dto.response.BaseResponse;
import com.wnc.internet_banking.dto.response.debtreminder.DebtReminderDto;
import com.wnc.internet_banking.service.DebtReminderService;
import com.wnc.internet_banking.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/debt-reminders")
@Tag(name = "Debt Reminder API", description = "Quản lý nhắc nợ")
public class DebtReminderController {

    private final DebtReminderService debtReminderService;

    @Operation(summary = "Tạo nhắc nợ", description = "Tạo một yêu cầu nhắc nợ mới")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tạo nhắc nợ thành công"),
            @ApiResponse(responseCode = "401", description = "Chưa đăng nhập hoặc token không hợp lệ",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "403", description = "Không có quyền truy cập",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    ResponseEntity<BaseResponse<DebtReminderDto>> createDebtReminder(
            @Parameter(description = "Thông tin nhắc nợ cần tạo")
            @Valid @RequestBody CreateDebtReminderRequest createDebtReminderRequest
    ) {
        UUID userId = SecurityUtil.getCurrentUserId();

        DebtReminderDto debtReminderDto = debtReminderService.createDebtReminder(createDebtReminderRequest, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.data(debtReminderDto));
    }

    @Operation(summary = "Lấy tất cả nhắc nợ", description = "Lấy danh sách nhắc nợ đã gửi và nhận")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "401", description = "Chưa đăng nhập hoặc token không hợp lệ",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping
    ResponseEntity<BaseResponse<Page<DebtReminderDto>>> getAllDebtReminders(
            @Parameter(description = "Trang hiện tại (bắt đầu từ 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Số lượng bản ghi mỗi trang", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Từ khóa tìm kiếm (tùy chọn)", example = "tiền nhà")
            @RequestParam(required = false) String query
    ) {
        UUID userId = SecurityUtil.getCurrentUserId();

        Page<DebtReminderDto> debtReminders;
        // If query is provided, filter by query
        if (query != null && !query.isEmpty()) {
            debtReminders = debtReminderService.searchAllDebtRemindersByUser(userId, query, page, size);
        } else {
            debtReminders = debtReminderService.getAllDebtRemindersByUser(userId, page, size);
        }

        return ResponseEntity.ok(BaseResponse.data(debtReminders));
    }

    @Operation(summary = "Lấy nhắc nợ đã gửi", description = "Lấy danh sách nhắc nợ do người dùng hiện tại gửi")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "401", description = "Chưa đăng nhập hoặc token không hợp lệ",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping("/sent")
    ResponseEntity<BaseResponse<Page<DebtReminderDto>>> getSentDebtReminders(
            @Parameter(description = "Trang hiện tại (bắt đầu từ 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Số lượng bản ghi mỗi trang", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Từ khóa tìm kiếm (tùy chọn)", example = "tiền học")
            @RequestParam(required = false) String query
    ) {
        UUID userId = SecurityUtil.getCurrentUserId();

        Page<DebtReminderDto> debtReminders;
        // If query is provided, filter by query
        if (query != null && !query.isEmpty()) {
            debtReminders = debtReminderService.searchSentDebtRemindersByUser(userId, query, page, size);
        } else {
            debtReminders = debtReminderService.getSentDebtRemindersByUser(userId, page, size);
        }

        return ResponseEntity.ok(BaseResponse.data(debtReminders));
    }

    @Operation(summary = "Lấy nhắc nợ đã nhận", description = "Lấy danh sách nhắc nợ mà người dùng hiện tại nhận được")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "401", description = "Chưa đăng nhập hoặc token không hợp lệ",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping("/received")
    ResponseEntity<BaseResponse<Page<DebtReminderDto>>> getReceivedDebtReminders(
            @Parameter(description = "Trang hiện tại (bắt đầu từ 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Số lượng bản ghi mỗi trang", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Từ khóa tìm kiếm (tùy chọn)", example = "tiền điện")
            @RequestParam(required = false) String query
    ) {
        UUID userId = SecurityUtil.getCurrentUserId();

        Page<DebtReminderDto> debtReminders;
        // If query is provided, filter by query
        if (query != null && !query.isEmpty()) {
            debtReminders = debtReminderService.searchReceivedDebtRemindersByUser(userId, query, page, size);
        } else {
            debtReminders = debtReminderService.getReceivedDebtRemindersByUser(userId, page, size);
        }

        return ResponseEntity.ok(BaseResponse.data(debtReminders));
    }

    @Operation(summary = "Hủy nhắc nợ đã gửi", description = "Người gửi có thể hủy yêu cầu nhắc nợ của mình")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Hủy nhắc nợ thành công"),
            @ApiResponse(responseCode = "401", description = "Chưa đăng nhập hoặc token không hợp lệ",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "403", description = "Không có quyền hủy nhắc nợ",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy nhắc nợ",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @DeleteMapping("/{debtReminderId}")
    @PreAuthorize(("hasRole('CUSTOMER') and @debtReminderService.isDebtReminderOwner(#debtReminderId, authentication.name)"))
    ResponseEntity<BaseResponse<?>> cancelDebtReminder(
            @Parameter(description = "ID nhắc nợ cần hủy", required = true)
            @PathVariable UUID debtReminderId,

            @Parameter(description = "Thông tin lý do hủy nhắc nợ")
            @RequestBody CancelDebtReminderRequest cancelDebtReminderRequest
    ) {
        debtReminderService.cancelDebtReminder(debtReminderId, cancelDebtReminderRequest);
        return ResponseEntity.ok(BaseResponse.message("Debt reminder cancelled successfully"));
    }

    @Operation(summary = "Từ chối nhắc nợ đã nhận", description = "Người nhận có thể từ chối nhắc nợ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Từ chối thành công"),
            @ApiResponse(responseCode = "401", description = "Chưa đăng nhập hoặc token không hợp lệ",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "403", description = "Không có quyền thao tác",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy nhắc nợ",
                    content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @DeleteMapping("/received/{debtReminderId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    ResponseEntity<BaseResponse<?>> cancelReceivedDebtReminder(
            @Parameter(description = "ID nhắc nợ đã nhận cần từ chối", required = true)
            @PathVariable UUID debtReminderId,

            @Parameter(description = "Thông tin lý do từ chối")
            @RequestBody CancelDebtReminderRequest cancelDebtReminderRequest
    ) {
        UUID userId = SecurityUtil.getCurrentUserId();

        debtReminderService.cancelReceivedDebtReminder(debtReminderId, cancelDebtReminderRequest, userId);
        return ResponseEntity.ok(BaseResponse.message("Received debt reminder cancelled successfully"));
    }
}
