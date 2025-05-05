package com.wnc.internet_banking.dto.request.transaction;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class InternalTransferRequest {
    private String senderAccountNumber;

    private String receiverAccountNumber;

    private Double amount;

    private String content;
}
