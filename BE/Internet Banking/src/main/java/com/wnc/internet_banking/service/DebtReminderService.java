package com.wnc.internet_banking.service;

import com.wnc.internet_banking.dto.request.debtreminder.CancelDebtReminderRequest;
import com.wnc.internet_banking.dto.request.debtreminder.CreateDebtReminderRequest;
import com.wnc.internet_banking.dto.response.debtreminder.DebtReminderDto;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface DebtReminderService {
    boolean isDebtReminderAlreadyPaid(UUID debtReminderId);

    void confirmDebtPayment(UUID debtReminderID);

    DebtReminderDto createDebtReminder(CreateDebtReminderRequest createDebtReminderRequest, UUID userId);

    boolean isDebtReminderOwner(UUID debtReminderId, String userId);

    DebtReminderDto getDebtReminderById(UUID debtReminderId);

    Page<DebtReminderDto> getDebtRemindersByUser(UUID userId, int page, int size);

    Page<DebtReminderDto> getReceivedDebtRemindersByUser(UUID userId, int page, int size);

    void cancelDebtReminder(UUID debtReminderId, CancelDebtReminderRequest cancelDebtReminderRequest);

    void cancelReceivedDebtReminder(UUID debtReminderId, CancelDebtReminderRequest cancelDebtReminderRequest, UUID userId);
}
