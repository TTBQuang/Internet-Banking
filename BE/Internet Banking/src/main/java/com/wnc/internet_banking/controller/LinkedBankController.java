package com.wnc.internet_banking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wnc.internet_banking.dto.request.account.AccountInfoRequestDto;
import com.wnc.internet_banking.dto.request.transaction.LinkedBankTransferRequestDto;
import com.wnc.internet_banking.dto.response.BaseResponse;
import com.wnc.internet_banking.dto.response.account.AccountDto;
import com.wnc.internet_banking.service.LinkedBankService;
import com.wnc.internet_banking.util.RSAUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/linked-banks")
public class LinkedBankController {
    private final LinkedBankService linkedBankService;
    private final ObjectMapper objectMapper;

    @Value("${rsa.private-key}")
    private String privateKey;

    @PostMapping("/account-info")
    public ResponseEntity<BaseResponse<?>> getAccountInfo(
            @RequestBody AccountInfoRequestDto accountInfoRequestDto,
            @RequestHeader("Bank-Code") String bankCode,
            @RequestHeader("X-Timestamp") String timestamp,
            @RequestHeader("X-Request-Hash") String requestHash) throws Exception {
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
        AccountDto accountInfo = linkedBankService.getAccountInfo(accountInfoRequestDto.getAccountNumber());

        // Serialize response data
        String dataString = objectMapper.writeValueAsString(accountInfo);

        // Sign response
        String signed = RSAUtils.sign(dataString, privateKey);

        return ResponseEntity.ok()
                .header("X-Signature", signed)
                .body(BaseResponse.data(accountInfo));
    }

    @PostMapping("/transfers")
    public ResponseEntity<?> transfers(
            @RequestBody LinkedBankTransferRequestDto linkedBankTransferRequestDto,
            @RequestHeader("Bank-Code") String bankCode,
            @RequestHeader("X-Timestamp") String timestamp,
            @RequestHeader("X-Request-Hash") String requestHash,
            @RequestHeader("X-Signature") String signature,
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
