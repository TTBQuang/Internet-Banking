package com.wnc.internet_banking.service;

import com.wnc.internet_banking.dto.request.debtreminder.CreateDebtReminderRequest;
import com.wnc.internet_banking.dto.response.debtreminder.DebtReminderDto;

import java.util.UUID;

public interface DebtReminderService {
    boolean isDebtReminderAlreadyPaid(UUID debtReminderId);
    void confirmDebtPayment(UUID debtReminderID);
    DebtReminderDto createDebtReminder(CreateDebtReminderRequest createDebtReminderRequest, UUID userId);
    DebtReminderDto getDebtReminderById(UUID debtReminderId);
}
