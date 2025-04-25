package com.wnc.internet_banking.controller;

import com.wnc.internet_banking.dto.request.recipient.RecipientCreateRequest;
import com.wnc.internet_banking.entity.Recipient;
import com.wnc.internet_banking.service.RecipientService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/recipients")
@AllArgsConstructor
public class RecipientController {
    private final RecipientService recipientService;

    @PostMapping
    public ResponseEntity<Recipient> createRecipient(@RequestBody RecipientCreateRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            UUID userId = UUID.fromString(authentication.getName());
            Recipient savedRecipient = recipientService.addRecipient(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedRecipient);
        } else {
            throw new AccessDeniedException("Unauthorized");
        }
    }
}
