package com.wnc.internet_banking.dto.request.recipient;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecipientCreateRequest {
    @NotBlank(message = "Recipient account number is required")
    private String accountNumber;

    @NotBlank(message = "Recipient nickname is required")
    private String nickname;

    private String bankCode;
}
