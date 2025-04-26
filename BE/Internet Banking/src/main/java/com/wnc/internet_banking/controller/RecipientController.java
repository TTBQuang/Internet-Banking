package com.wnc.internet_banking.controller;

import com.wnc.internet_banking.dto.request.recipient.RecipientCreateRequest;
import com.wnc.internet_banking.dto.response.BaseResponse;
import com.wnc.internet_banking.dto.response.recipient.RecipientDto;
import com.wnc.internet_banking.service.RecipientService;
import com.wnc.internet_banking.util.SecurityUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/recipients")
@AllArgsConstructor
public class RecipientController {
    private final RecipientService recipientService;

    @PostMapping
    public ResponseEntity<BaseResponse<RecipientDto>> createRecipient(@RequestBody RecipientCreateRequest request) {
        UUID userId = SecurityUtil.getCurrentUserId();
        RecipientDto savedRecipient = recipientService.addRecipient(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.data(savedRecipient));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@recipientService.isRecipientOwner(#id, authentication.name)")
    public ResponseEntity<BaseResponse<Void>> deleteRecipient(@PathVariable UUID id) {
        recipientService.deleteRecipient(id);
        return ResponseEntity.ok(BaseResponse.message("Recipient deleted successfully"));
    }
}
