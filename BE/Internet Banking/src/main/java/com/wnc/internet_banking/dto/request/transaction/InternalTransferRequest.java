package com.wnc.internet_banking.dto.request.transaction;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class InternalTransferRequest {
    private String receiverAccountNumber;

    private Double amount;

    private String content;
}
