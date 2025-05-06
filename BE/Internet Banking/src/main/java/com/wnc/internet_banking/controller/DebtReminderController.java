package com.wnc.internet_banking.controller;

import com.wnc.internet_banking.dto.request.debtreminder.CreateDebtReminderRequest;
import com.wnc.internet_banking.dto.response.BaseResponse;
import com.wnc.internet_banking.dto.response.debtreminder.DebtReminderDto;
import com.wnc.internet_banking.service.DebtReminderService;
import com.wnc.internet_banking.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
