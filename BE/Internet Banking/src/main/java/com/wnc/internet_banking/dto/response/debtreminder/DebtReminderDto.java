package com.wnc.internet_banking.dto.response.debtreminder;

import com.wnc.internet_banking.dto.response.account.AccountDto;
import com.wnc.internet_banking.dto.response.user.UserDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class DebtReminderDto {

    private UUID debtReminderId;

    private UserDto creditor;

    private AccountDto debtorAccount;

    private Double amount;

    private String content;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime paidAt;
}
