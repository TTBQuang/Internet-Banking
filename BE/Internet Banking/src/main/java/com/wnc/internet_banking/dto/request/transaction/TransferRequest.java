package com.wnc.internet_banking.dto.request.transaction;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TransferRequest {
    private String receiverBankCode;
    private String receiverAccountNumber;

    private Double amount;

    private String content;
}
