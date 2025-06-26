package com.wnc.internet_banking.controller;

import com.wnc.internet_banking.dto.request.recipient.RecipientCreateRequest;
import com.wnc.internet_banking.dto.request.recipient.RecipientUpdateRequest;
import com.wnc.internet_banking.dto.response.BaseResponse;
import com.wnc.internet_banking.dto.response.recipient.RecipientDto;
import com.wnc.internet_banking.service.RecipientService;
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

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/recipients")
@Tag(name = "Recipient API", description = "Quản lý người nhận")
public class RecipientController {
    private final RecipientService recipientService;

    @Operation(summary = "Thêm người nhận", description = "Thêm mới một người nhận cho người dùng hiện tại")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tạo thành công"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Tham số không hợp lệ",
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
                    description = "Không có quyền truy cập",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class)
                    )
            ),
    })
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BaseResponse<RecipientDto>> createRecipient(@RequestBody RecipientCreateRequest request) {
        UUID userId = SecurityUtil.getCurrentUserId();
        RecipientDto savedRecipient = recipientService.addRecipient(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.data(savedRecipient));
    }

    @Operation(summary = "Xóa người nhận", description = "Xóa một người nhận của người dùng hiện tại")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Xóa thành công"),
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
                    description = "Không tìm thấy người nhận",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class)
                    )
            ),
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("@recipientService.isRecipientOwner(#id, authentication.name) and hasRole('CUSTOMER')")
    public ResponseEntity<BaseResponse<Void>> deleteRecipient(
            @PathVariable @Parameter(description = "ID của người nhận") UUID id) {
        recipientService.deleteRecipient(id);
        return ResponseEntity.ok(BaseResponse.message("Recipient deleted successfully"));
    }

    @Operation(summary = "Lấy danh sách người nhận có phân trang", description = "Trả về danh sách người nhận theo trang")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công"),
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
    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BaseResponse<Page<RecipientDto>>> getRecipients(
            @RequestParam(defaultValue = "0") @Parameter(description = "Trang cần lấy") int page,
            @RequestParam(defaultValue = "10") @Parameter(description = "Số phần tử mỗi trang") int size,
            @RequestParam(required = false) @Parameter(description = "Tên gợi nhớ (nickname) để tìm kiếm") String nickname
    ) {
        UUID userId = SecurityUtil.getCurrentUserId();
        Page<RecipientDto> recipients = recipientService.getRecipientsByUserAndNickname(userId, nickname, page, size);
        return ResponseEntity.ok(BaseResponse.data(recipients));
    }

    @Operation(summary = "Lấy tất cả người nhận", description = "Trả về danh sách tất cả người nhận của người dùng hiện tại")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công"),
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
    @GetMapping("/all")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BaseResponse<List<RecipientDto>>> getAllRecipients() {
        UUID userId = SecurityUtil.getCurrentUserId();
        List<RecipientDto> recipients = recipientService.getRecipientsByUser(userId);
        return ResponseEntity.ok(BaseResponse.data(recipients));
    }

    @Operation(summary = "Lấy danh sách người nhận nội bộ", description = "Trả về danh sách người nhận cùng ngân hàng (nội bộ)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công"),
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
    @GetMapping("/all/internal")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'EMPLOYEE')")
    public ResponseEntity<BaseResponse<List<RecipientDto>>> getAllInternalRecipients(
            @RequestParam(required = false) @Parameter(description = "Tên gợi nhớ để tìm kiếm") String nickname
    ) {
        UUID userId = SecurityUtil.getCurrentUserId();

        List<RecipientDto> recipients = (nickname != null && !nickname.isEmpty())
                ? recipientService.getAllInternalRecipientsByUserAndNickname(userId, nickname)
                : recipientService.getAllInternalRecipientsByUser(userId);

        return ResponseEntity.ok(BaseResponse.data(recipients));
    }

    @Operation(summary = "Cập nhật tên gợi nhớ người nhận", description = "Chỉnh sửa nickname của người nhận")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
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
                    description = "Không tìm thấy người nhận",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class)
                    )
            ),
    })
    @PutMapping("/{id}")
    @PreAuthorize("@recipientService.isRecipientOwner(#id, authentication.name) and hasRole('CUSTOMER')")
    public ResponseEntity<BaseResponse<RecipientDto>> updateRecipientNickname(
            @PathVariable @Parameter(description = "ID người nhận") UUID id,
            @Valid @RequestBody RecipientUpdateRequest request
    ) {
        RecipientDto updatedRecipient = recipientService.updateRecipientNickname(id, request);
        return ResponseEntity.ok(BaseResponse.data(updatedRecipient));
    }
}
