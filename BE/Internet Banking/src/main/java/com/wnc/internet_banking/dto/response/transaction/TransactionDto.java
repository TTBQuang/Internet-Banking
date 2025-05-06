package com.wnc.internet_banking.dto.response.transaction;

import com.wnc.internet_banking.dto.response.linkedbank.LinkedBankDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class TransactionDto {
    private UUID transactionId;

    private String senderAccountNumber;

    private LinkedBankDto senderBank;

    private String receiverAccountNumber;

    private LinkedBankDto receiverBank;

    private Double amount;

    private Double fee;

    private String feePayer;

    private String content;

    private String type;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime confirmedAt;
}
