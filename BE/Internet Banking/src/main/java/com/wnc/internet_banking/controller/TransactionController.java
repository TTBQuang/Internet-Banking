package com.wnc.internet_banking.controller;

import com.wnc.internet_banking.dto.request.transaction.ConfirmDebtPaymentRequest;
import com.wnc.internet_banking.dto.request.transaction.ConfirmTransactionRequest;
import com.wnc.internet_banking.dto.request.transaction.DebtPaymentRequest;
import com.wnc.internet_banking.dto.request.transaction.TransferRequest;
import com.wnc.internet_banking.dto.response.BaseResponse;
import com.wnc.internet_banking.dto.response.transaction.TransactionDto;
import com.wnc.internet_banking.entity.Transaction;
import com.wnc.internet_banking.service.TransactionService;
import com.wnc.internet_banking.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
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

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/transactions")
@Tag(name = "Transaction API", description = "Quản lý giao dịch")
public class TransactionController {
    private final TransactionService transactionService;

    @GetMapping("/histories/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BaseResponse<List<Transaction>>> getTransactionHistoryForCustomer(
            @RequestParam String accountNumber) {
        List<Transaction> transactions = transactionService.getTransactionHistory(accountNumber);
        return ResponseEntity.ok(BaseResponse.data(transactions));
    }

    @GetMapping("/histories")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<BaseResponse<List<Transaction>>> getTransactionHistoryForEmployee(
            @RequestParam String accountNumber) {
        List<Transaction> transactions = transactionService.getTransactionHistory(accountNumber);
        return ResponseEntity.ok(BaseResponse.data(transactions));
    }

    @Operation(summary = "Lấy các giao dịch chuyển khoản", description = "Lấy danh sách giao dịch chuyển khoản đã thực hiện bởi người dùng hiện tại")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "401", description = "Chưa đăng nhập hoặc token không hợp lệ", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping("/transfer/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BaseResponse<Page<TransactionDto>>> getCurrentUserTransferTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        UUID userId = SecurityUtil.getCurrentUserId();

        Page<TransactionDto> transactions = transactionService.getTransferTransactionsByUser(userId, page, size);

        return ResponseEntity.ok(BaseResponse.data(transactions));
    }

    @Operation(summary = "Lấy các giao dịch nhận tiền", description = "Lấy danh sách giao dịch nhận tiền của người dùng hiện tại")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "401", description = "Chưa đăng nhập hoặc token không hợp lệ", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping("/received/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BaseResponse<Page<TransactionDto>>> getCurrentUserReceivedTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        UUID userId = SecurityUtil.getCurrentUserId();

        Page<TransactionDto> transactions = transactionService.getReceivedTransactionsByUser(userId, page, size);

        return ResponseEntity.ok(BaseResponse.data(transactions));
    }

    @Operation(summary = "Lấy các giao dịch thanh toán nhắc nợ", description = "Lấy danh sách các giao dịch thanh toán nhắc nợ của người dùng hiện tại")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "401", description = "Chưa đăng nhập hoặc token không hợp lệ", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping("/debt-payment/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BaseResponse<Page<TransactionDto>>> getCurrentUserDebtPaymentTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        UUID userId = SecurityUtil.getCurrentUserId();

        Page<TransactionDto> transactions = transactionService.getDebtPaymentTransactionsByUser(userId, page, size);

        return ResponseEntity.ok(BaseResponse.data(transactions));
    }

    @Operation(summary = "Khởi tạo giao dịch chuyển tiền", description = "Khởi tạo một giao dịch chuyển tiền chờ xác nhận OTP")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tạo giao dịch thành công"),
            @ApiResponse(responseCode = "400", description = "Yêu cầu không hợp lệ", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "401", description = "Chưa đăng nhập", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @PostMapping("/transfers")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BaseResponse<UUID>> initiateTransfer(
            @RequestBody TransferRequest transferRequest
    ) {
        UUID userId = SecurityUtil.getCurrentUserId();

        UUID transactionId = transactionService.initiateTransfer(transferRequest, userId);

        return ResponseEntity.ok(BaseResponse.data(transactionId));
    }

    @Operation(summary = "Xác nhận giao dịch chuyển tiền", description = "Xác nhận một giao dịch chuyển tiền bằng mã OTP")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Xác nhận thành công"),
            @ApiResponse(responseCode = "400", description = "OTP không hợp lệ hoặc giao dịch không hợp lệ", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy giao dịch", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @PostMapping("/transfers/{transactionId}/confirm")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BaseResponse<?>> confirmTransfer(
            @PathVariable UUID transactionId,
            @RequestBody ConfirmTransactionRequest confirmTransactionRequest
    ) {
        UUID userId = SecurityUtil.getCurrentUserId();

        transactionService.confirmTransfer(transactionId, confirmTransactionRequest, userId);

        return ResponseEntity.ok(BaseResponse.message("Transaction confirmed successfully"));
    }

    @Operation(summary = "Khởi tạo giao dịch thanh toán nhắc nợ", description = "Khởi tạo giao dịch thanh toán cho nhắc nợ đã nhận")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Khởi tạo thành công"),
            @ApiResponse(responseCode = "400", description = "Yêu cầu không hợp lệ", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "401", description = "Chưa đăng nhập", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @PostMapping("/debt-payment")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BaseResponse<UUID>> initiateDebtPayment(
            @RequestBody DebtPaymentRequest debtPaymentRequest
    ) {
        UUID userId = SecurityUtil.getCurrentUserId();

        UUID transactionId = transactionService.initiateDebtPayment(debtPaymentRequest, userId);

        return ResponseEntity.ok(BaseResponse.data(transactionId));
    }

    @Operation(summary = "Xác nhận thanh toán nhắc nợ", description = "Xác nhận thanh toán bằng mã OTP")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Xác nhận thành công"),
            @ApiResponse(responseCode = "400", description = "OTP không hợp lệ", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy giao dịch", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @PostMapping("/debt-payment/{transactionId}/confirm")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BaseResponse<?>> confirmDebtPayment(
            @PathVariable UUID transactionId,
            @RequestBody ConfirmDebtPaymentRequest confirmDebtPaymentRequest
    ) {
        UUID userId = SecurityUtil.getCurrentUserId();

        transactionService.confirmDebtPayment(transactionId, confirmDebtPaymentRequest, userId);

        return ResponseEntity.ok(BaseResponse.message("Transaction confirmed successfully"));
    }
}
