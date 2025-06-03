package com.wnc.internet_banking.controller;

import com.wnc.internet_banking.dto.response.BaseResponse;
import com.wnc.internet_banking.dto.response.notification.NotificationDto;
import com.wnc.internet_banking.service.NotificationService;
import com.wnc.internet_banking.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BaseResponse<List<NotificationDto>>> getAllNotifications() {
        UUID userId = SecurityUtil.getCurrentUserId();
        List<NotificationDto> notifications = notificationService.getAllNotificationsByUserId(userId);
        return ResponseEntity.ok(BaseResponse.data(notifications));
    }

    @PutMapping("/{id}/read")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BaseResponse<Void>> markAsRead(@PathVariable @Parameter(description = "Notification UUID") UUID id) {
        UUID userId = SecurityUtil.getCurrentUserId();
        notificationService.markAsRead(userId, id);
        return ResponseEntity.ok(BaseResponse.message("Marked as read"));
    }

    @PutMapping("/read-all")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<BaseResponse<Void>> markAllAsRead() {
        UUID userId = SecurityUtil.getCurrentUserId();
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(BaseResponse.message("Marked as read"));
    }
}