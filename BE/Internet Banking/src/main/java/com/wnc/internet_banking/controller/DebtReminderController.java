package com.wnc.internet_banking.controller;

import com.wnc.internet_banking.dto.request.debtreminder.CancelDebtReminderRequest;
import com.wnc.internet_banking.dto.request.debtreminder.CreateDebtReminderRequest;
import com.wnc.internet_banking.dto.response.BaseResponse;
import com.wnc.internet_banking.dto.response.debtreminder.DebtReminderDto;
import com.wnc.internet_banking.service.DebtReminderService;
import com.wnc.internet_banking.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/debt-reminders")
public class DebtReminderController {

    private final DebtReminderService debtReminderService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    ResponseEntity<BaseResponse<DebtReminderDto>> createDebtReminder (
            @RequestBody CreateDebtReminderRequest createDebtReminderRequest
    ) {
        UUID userId = SecurityUtil.getCurrentUserId();

        DebtReminderDto debtReminderDto = debtReminderService.createDebtReminder(createDebtReminderRequest, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.data(debtReminderDto));
    }

    @GetMapping
    ResponseEntity<BaseResponse<Page<DebtReminderDto>>> getDebtReminders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        UUID userId = SecurityUtil.getCurrentUserId();

        Page<DebtReminderDto> debtReminders = debtReminderService.getDebtRemindersByUser(userId, page, size);
        return ResponseEntity.ok(BaseResponse.data(debtReminders));
    }

    @GetMapping("/received")
    ResponseEntity<BaseResponse<Page<DebtReminderDto>>> getReceivedDebtReminder(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        UUID userId = SecurityUtil.getCurrentUserId();

        Page<DebtReminderDto> debtReminders = debtReminderService.getReceivedDebtRemindersByUser(userId, page, size);
        return ResponseEntity.ok(BaseResponse.data(debtReminders));
    }

    @DeleteMapping("/{debtReminderId}")
    @PreAuthorize(("hasRole('CUSTOMER') and @debtReminderService.isDebtReminderOwner(#debtReminderId, authentication.name)"))
    ResponseEntity<BaseResponse<?>> cancelDebtReminder(
            @PathVariable UUID debtReminderId,
            @RequestBody CancelDebtReminderRequest cancelDebtReminderRequest
    ) {
        debtReminderService.cancelDebtReminder(debtReminderId, cancelDebtReminderRequest);
        return ResponseEntity.ok(BaseResponse.message("Debt reminder cancelled successfully"));
    }


    @DeleteMapping("/received/{debtReminderId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    ResponseEntity<BaseResponse<?>> cancelReceivedDebtReminder(
            @PathVariable UUID debtReminderId,
            @RequestBody CancelDebtReminderRequest cancelDebtReminderRequest
    ) {
        UUID userId = SecurityUtil.getCurrentUserId();

        debtReminderService.cancelReceivedDebtReminder(debtReminderId, cancelDebtReminderRequest, userId);
        return ResponseEntity.ok(BaseResponse.message("Received debt reminder cancelled successfully"));
    }
}
