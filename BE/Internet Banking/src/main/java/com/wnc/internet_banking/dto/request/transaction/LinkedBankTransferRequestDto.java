package com.wnc.internet_banking.dto.request.transaction;

import lombok.Data;

@Data
public class LinkedBankTransferRequestDto {
    private String senderAccountNumber;
    private String receiverAccountNumber;
    private Double amount;
    private String content;
}
