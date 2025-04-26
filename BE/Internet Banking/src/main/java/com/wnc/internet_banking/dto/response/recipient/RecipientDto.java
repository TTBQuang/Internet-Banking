package com.wnc.internet_banking.dto.response.recipient;

import com.wnc.internet_banking.dto.response.linked_bank.LinkedBankDto;
import com.wnc.internet_banking.dto.response.user.UserDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class RecipientDto {
    private UUID recipientId;
    private UserDto owner;
    private String accountNumber;
    private String nickname;
    private LinkedBankDto bank;
    private LocalDateTime createdAt;
}
