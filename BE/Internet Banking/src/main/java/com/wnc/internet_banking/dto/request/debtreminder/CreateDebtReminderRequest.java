package com.wnc.internet_banking.dto.request.debtreminder;


import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateDebtReminderRequest {
    private String debtorAccountNumber;

    @Positive(message = "Amount must be positive")
    private Double amount;

    private String content;
}
