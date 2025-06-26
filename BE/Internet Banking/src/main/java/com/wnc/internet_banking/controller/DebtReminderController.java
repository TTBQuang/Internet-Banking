package com.wnc.internet_banking.controller;

import com.wnc.internet_banking.dto.request.debtreminder.CancelDebtReminderRequest;
import com.wnc.internet_banking.dto.request.debtreminder.CreateDebtReminderRequest;
import com.wnc.internet_banking.dto.response.BaseResponse;
import com.wnc.internet_banking.dto.response.debtreminder.DebtReminderDto;
import com.wnc.internet_banking.service.DebtReminderService;
import com.wnc.internet_banking.util.SecurityUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@Tag(name = "Debt Reminder API", description = "Quản lý nhắc nợ")
public class DebtReminderController {

    private final DebtReminderService debtReminderService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    ResponseEntity<BaseResponse<DebtReminderDto>> createDebtReminder (
            @Valid @RequestBody CreateDebtReminderRequest createDebtReminderRequest
    ) {
        UUID userId = SecurityUtil.getCurrentUserId();

        DebtReminderDto debtReminderDto = debtReminderService.createDebtReminder(createDebtReminderRequest, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.data(debtReminderDto));
    }

    @GetMapping
    ResponseEntity<BaseResponse<Page<DebtReminderDto>>> getAllDebtReminders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String query
    ) {
        UUID userId = SecurityUtil.getCurrentUserId();

        Page<DebtReminderDto> debtReminders;
        // If query is provided, filter by query
        if (query != null && !query.isEmpty()) {
            debtReminders = debtReminderService.searchAllDebtRemindersByUser(userId, query, page, size);
        } else {
            debtReminders = debtReminderService.getAllDebtRemindersByUser(userId, page, size);
        }

        return ResponseEntity.ok(BaseResponse.data(debtReminders));
    }

    @GetMapping("/sent")
    ResponseEntity<BaseResponse<Page<DebtReminderDto>>> getSentDebtReminders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String query
    ) {
        UUID userId = SecurityUtil.getCurrentUserId();

        Page<DebtReminderDto> debtReminders;
        // If query is provided, filter by query
        if (query != null && !query.isEmpty()) {
            debtReminders = debtReminderService.searchSentDebtRemindersByUser(userId, query, page, size);
        } else {
            debtReminders = debtReminderService.getSentDebtRemindersByUser(userId, page, size);
        }

        return ResponseEntity.ok(BaseResponse.data(debtReminders));
    }

    @GetMapping("/received")
    ResponseEntity<BaseResponse<Page<DebtReminderDto>>> getReceivedDebtReminders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String query
    ) {
        UUID userId = SecurityUtil.getCurrentUserId();

        Page<DebtReminderDto> debtReminders;
        // If query is provided, filter by query
        if( query != null && !query.isEmpty()) {
            debtReminders = debtReminderService.searchReceivedDebtRemindersByUser(userId, query, page, size);
        } else {
            debtReminders = debtReminderService.getReceivedDebtRemindersByUser(userId, page, size);
        }

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
