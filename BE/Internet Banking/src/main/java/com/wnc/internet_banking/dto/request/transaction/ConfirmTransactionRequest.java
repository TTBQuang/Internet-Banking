package com.wnc.internet_banking.dto.request.transaction;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfirmTransactionRequest {
    private String otpCode;
}
