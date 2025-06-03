package com.wnc.internet_banking.service;

import com.wnc.internet_banking.dto.request.transaction.LinkedBankTransferRequestDto;
import com.wnc.internet_banking.dto.response.account.AccountDto;

public interface LinkedBankService {
    AccountDto getAccountInfo(String accountNumber);

    void transfers(LinkedBankTransferRequestDto requestDto, String bankCode);

    boolean verifyTimestamp(String timestamp);

    boolean verifyRequestHash(String data, String timestamp, String bankCode, String requestHash) throws Exception;

    boolean verifySignature(String bankCode, String data, String signature) throws Exception;
}
