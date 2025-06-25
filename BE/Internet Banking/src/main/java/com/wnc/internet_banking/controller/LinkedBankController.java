package com.wnc.internet_banking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wnc.internet_banking.dto.request.account.AccountInfoRequestDto;
import com.wnc.internet_banking.dto.request.transaction.LinkedBankTransferRequestDto;
import com.wnc.internet_banking.dto.response.BaseResponse;
import com.wnc.internet_banking.dto.response.account.AccountDto;
import com.wnc.internet_banking.dto.response.linkedbank.AccountResponseDto;
import com.wnc.internet_banking.dto.response.linkedbank.LinkedBankDto;
import com.wnc.internet_banking.service.LinkedBankService;
import com.wnc.internet_banking.util.RSAUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/linked-banks")
@Tag(name = "Linked Bank API", description = "Quản lý ngân hàng liên kết")
public class LinkedBankController {
    private final LinkedBankService linkedBankService;
    private final ObjectMapper objectMapper;

    @Value("${rsa.private-key}")
    private String privateKey;

    @GetMapping
    @Operation(
            summary = "Lấy danh sách tất cả ngân hàng liên kết",
            description = "API này trả về danh sách các ngân hàng đã được liên kết trong hệ thống.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Danh sách ngân hàng liên kết được trả về thành công"
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
                    @ApiResponse(
                            responseCode = "500",
                            description = "Lỗi hệ thống",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BaseResponse.class)
                            )
                    )
            }
    )
    public ResponseEntity<BaseResponse<List<LinkedBankDto>>> getAllLinkedBanks() {
        List<LinkedBankDto> allLinkedBanks = linkedBankService.getAllLinkedBanks();

        return ResponseEntity.ok(BaseResponse.data(allLinkedBanks));
    }

    @GetMapping("/account-info")
    @Operation(
            summary = "Truy vấn thông tin tài khoản",
            description = "Trả về thông tin tài khoản theo mã ngân hàng và số tài khoản",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Truy vấn thành công"
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
                    @ApiResponse(
                            responseCode = "404",
                            description = "Không tìm thấy người nhận",
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
            }
    )
    public ResponseEntity<BaseResponse<AccountResponseDto>> getAccountInfo(
            @RequestParam(name = "bankCode", required = true)
            @Parameter(description = "Mã ngân hàng", example = "FIN") String bankCode,
            @RequestParam(name = "accountNumber", required = true)
            @Parameter(description = "Số tài khoản", example = "5873906278933357")String accountNumber
    ) throws Exception {
        AccountResponseDto accountInfo = linkedBankService.getAccountInfo(bankCode, accountNumber);
        return ResponseEntity.ok(BaseResponse.data(accountInfo));
    }

    @PostMapping("/account-info")
    @Operation(
            summary = "Truy vấn thông tin tài khoản (có xác thực Hash và ký số)",
            description = "API truy vấn thông tin tài khoản theo số tài khoản, yêu cầu xác thực bằng HMAC và trả về chữ ký RSA trong header",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Truy vấn thành công",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BaseResponse.class),
                                    examples = @ExampleObject(value = """
                                    {
                                      "message": "Success",
                                      "data": {
                                        "accountNumber": "ACC7A0C68BADB",
                                        "fullName": "Ngo Thanh Loc"
                                      }
                                    }
                                    """)
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
                    @ApiResponse(
                            responseCode = "404",
                            description = "Không tìm thấy người nhận",
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
            }
    )
    public ResponseEntity<BaseResponse<AccountResponseDto>> getAccountInfo(
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Thông tin truy vấn tài khoản",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AccountInfoRequestDto.class)
            )) AccountInfoRequestDto accountInfoRequestDto,
            @RequestHeader("Bank-Code")
            @Parameter(description = "Mã ngân hàng yêu cầu", example = "FIN", required = true) String bankCode,
            @RequestHeader("X-Timestamp")
            @Parameter(description = "Thời gian gửi yêu cầu (milliseconds kể từ 1900-01-01T00:00:00Z)",
                    example = "1750842838629", required = true) String timestamp,
            @RequestHeader("X-Request-Hash")
            @Parameter(description = "HMAC SHA256 dùng để xác thực nội dung yêu cầu hashInput = body + timestamp + bankCode + secretKey",
                    example = "84e5e77dd17b4b...", required = true)
            String requestHash) throws Exception {
        // Verify timestamp
        if (!linkedBankService.verifyTimestamp(timestamp)) {
            return ResponseEntity.ok(BaseResponse.message("Invalid or expired timestamp"));
        }

        // Verify Request Hash
        if (!linkedBankService.verifyRequestHash(
                objectMapper.writeValueAsString(accountInfoRequestDto), timestamp, bankCode, requestHash)) {
            return ResponseEntity.ok(BaseResponse.message("Invalid request hash"));
        }

        // Get account info
        AccountResponseDto accountInfo = linkedBankService.getAccountInfo(accountInfoRequestDto.getAccountNumber());

        // Serialize response data
        String dataString = objectMapper.writeValueAsString(accountInfo);

        // Sign response
        String signed = RSAUtils.sign(dataString, privateKey);

        return ResponseEntity.ok()
                .header("X-Signature", signed)
                .body(BaseResponse.data(accountInfo));
    }

    @PostMapping("/transfers")
    @Operation(
            summary = "Chuyển tiền liên ngân hàng",
            description = "Chuyển tiền đến tài khoản ở ngân hàng khác. Yêu cầu xác thực bằng timestamp, HMAC và chữ ký RSA.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Giao dịch thành công",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BaseResponse.class),
                                    examples = @ExampleObject(value = """
                                    {
                                      "message": "Transfer successful.",
                                      "data": null
                                    }
                                    """)
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
                    @ApiResponse(
                            responseCode = "404",
                            description = "Không tìm thấy người nhận",
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
            }
    )
    public ResponseEntity<?> transfers(
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Thông tin chuyển khoản",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LinkedBankTransferRequestDto.class),
                            examples = @ExampleObject(value = """
                            {
                              "fromAccount": "ACC7A0C68BADB",
                              "toAccount": "5873906278933357",
                              "toBankCode": "FIN",
                              "amount": 100000,
                              "content": "Chuyen tien cho Nguyen Thi A"
                            }
                            """)
                    )
            )
            LinkedBankTransferRequestDto linkedBankTransferRequestDto,
            @RequestHeader("Bank-Code")
            @Parameter(description = "Mã ngân hàng gửi yêu cầu", example = "FIN", required = true) String bankCode,
            @RequestHeader("X-Timestamp")
            @Parameter(description = "Thời gian gửi yêu cầu (mili-giây từ 1900-01-01T00:00:00Z)",
                    example = "1750842838629", required = true) String timestamp,
            @RequestHeader("X-Request-Hash")
            @Parameter(description = "HMAC SHA256 dùng để xác thực nội dung yêu cầu hashInput = body + timestamp + bankCode + secretKey",
                    example = "84e5e77dd17b4b...", required = true) String requestHash,
            @RequestHeader("X-Signature")
            @Parameter(description = "Chữ ký RSA của nội dung request (signed JSON body chuẩn hóa)",
                    example = "base64encodedSignature", required = true) String signature,
            HttpServletRequest servletRequest) throws Exception {

        String rawBody = getRawBody(servletRequest);
        String compactBody = normalizeJson(rawBody);

        // Verify timestamp
        if (!linkedBankService.verifyTimestamp(timestamp)) {
            return ResponseEntity.ok(BaseResponse.message("Invalid or expired timestamp"));
        }

        // Verify Request Hash
        if (!linkedBankService.verifyRequestHash(compactBody, timestamp, bankCode, requestHash)) {
            return ResponseEntity.ok(BaseResponse.message("Invalid request hash"));
        }

        // Verify Signature
        boolean valid = linkedBankService.verifySignature(bankCode, compactBody, signature);
        if (!valid) {
            return ResponseEntity.ok(BaseResponse.message("Invalid signature"));
        }

        // Serialize response data
        linkedBankService.transfers(linkedBankTransferRequestDto, bankCode);

        // Sign response
        BaseResponse<String> response = BaseResponse.message("Transfer successful.");
        String responseJson = objectMapper.writeValueAsString(response);
        String signed = RSAUtils.sign(responseJson, privateKey);

        return ResponseEntity.ok()
                .header("X-Signature", signed)
                .body(BaseResponse.message("Transfer successful."));
    }

    private String getRawBody(HttpServletRequest request) throws IOException {
        ContentCachingRequestWrapper wrapper = (ContentCachingRequestWrapper) request;
        byte[] buf = wrapper.getContentAsByteArray();
        return new String(buf, StandardCharsets.UTF_8);
    }

    private String normalizeJson(String rawBody) throws IOException {
        Object json = objectMapper.readValue(rawBody, Object.class);
        return objectMapper.writeValueAsString(json); // compact JSON (no pretty print)
    }
}
