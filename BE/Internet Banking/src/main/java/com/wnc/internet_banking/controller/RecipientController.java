package com.wnc.internet_banking.controller;

import com.wnc.internet_banking.dto.request.recipient.RecipientCreateRequest;
import com.wnc.internet_banking.dto.request.recipient.RecipientUpdateRequest;
import com.wnc.internet_banking.dto.response.BaseResponse;
import com.wnc.internet_banking.dto.response.recipient.RecipientDto;
import com.wnc.internet_banking.service.RecipientService;
import com.wnc.internet_banking.util.SecurityUtil;
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
@RequestMapping("/recipients")
public class RecipientController {
    private final RecipientService recipientService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BaseResponse<RecipientDto>> createRecipient(@RequestBody RecipientCreateRequest request) {
        UUID userId = SecurityUtil.getCurrentUserId();
        RecipientDto savedRecipient = recipientService.addRecipient(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.data(savedRecipient));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@recipientService.isRecipientOwner(#id, authentication.name) and hasRole('CUSTOMER')")
    public ResponseEntity<BaseResponse<Void>> deleteRecipient(@PathVariable UUID id) {
        recipientService.deleteRecipient(id);
        return ResponseEntity.ok(BaseResponse.message("Recipient deleted successfully"));
    }

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BaseResponse<Page<RecipientDto>>> getRecipients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String nickname
    ) {
        UUID userId = SecurityUtil.getCurrentUserId();
        Page<RecipientDto> recipients = recipientService.getRecipientsByUserAndNickname(userId, nickname, page, size);
        return ResponseEntity.ok(BaseResponse.data(recipients));
    }


    @PutMapping("/{id}")
    @PreAuthorize("@recipientService.isRecipientOwner(#id, authentication.name) and hasRole('CUSTOMER')")
    public ResponseEntity<BaseResponse<RecipientDto>> updateRecipientNickname(
            @PathVariable UUID id,
            @Valid @RequestBody RecipientUpdateRequest request
    ) {
        RecipientDto updatedRecipient = recipientService.updateRecipientNickname(id, request);
        return ResponseEntity.ok(BaseResponse.data(updatedRecipient));
    }
}
