package com.wnc.internet_banking.dto.request.debtreminder;


import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateDebtReminderRequest {
    private String debtorAccountNumber;

    @Positive
    private Double amount;

    private String content;
}
