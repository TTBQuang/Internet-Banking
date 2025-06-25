package com.wnc.internet_banking.service;

import com.wnc.internet_banking.dto.request.transaction.LinkedBankTransferRequestDto;
import com.wnc.internet_banking.dto.response.account.AccountDto;
import com.wnc.internet_banking.dto.response.linkedbank.AccountResponseDto;
import com.wnc.internet_banking.dto.response.linkedbank.LinkedBankDto;
import com.wnc.internet_banking.entity.Transaction;

import java.util.List;

public interface LinkedBankService {
    List<LinkedBankDto> getAllLinkedBanks();

    AccountResponseDto getAccountInfo(String accountNumber);

    void transfers(LinkedBankTransferRequestDto requestDto, String bankCode);

    boolean verifyTimestamp(String timestamp);

    boolean verifyRequestHash(String data, String timestamp, String bankCode, String requestHash) throws Exception;

    boolean verifySignature(String bankCode, String data, String signature) throws Exception;

    AccountResponseDto getAccountInfo(String bankCode, String accountNumber) throws Exception;

    void sendTransferRequest(Transaction transaction) throws Exception;
}
