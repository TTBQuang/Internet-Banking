package com.wnc.internet_banking.dto.request.recipient;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecipientUpdateRequest {
    @NotBlank(message = "Nickname is required")
    private String nickname;
}
