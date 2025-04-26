package com.wnc.internet_banking.dto.response.linked_bank;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class LinkedBankDto {
    private UUID linkedBankId;
    private String bankCode;
    private String bankName;
}
