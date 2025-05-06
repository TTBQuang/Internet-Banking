package com.wnc.internet_banking.dto.request.transaction;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class DebtPaymentRequest {

    private String debtorAccountNumber;

    private String creditorAccountNumber;

    private String content;

    private UUID debtReminderId;

}
