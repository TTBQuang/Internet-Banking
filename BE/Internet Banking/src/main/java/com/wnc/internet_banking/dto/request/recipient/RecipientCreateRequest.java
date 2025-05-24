package com.wnc.internet_banking.dto.request.recipient;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class RecipientCreateRequest {
    @NotBlank(message = "Recipient account number is required")
    private String accountNumber;

    private String nickname;

    private UUID bankId;

    private String fullName; // Not null when bankCode is not null
}
