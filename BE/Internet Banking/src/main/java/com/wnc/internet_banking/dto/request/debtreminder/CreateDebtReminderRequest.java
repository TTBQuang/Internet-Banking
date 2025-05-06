package com.wnc.internet_banking.dto.request.debtreminder;


import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateDebtReminderRequest {
    private UUID debtorAccountId;

    @Positive
    private Double amount;

    private String content;
}
