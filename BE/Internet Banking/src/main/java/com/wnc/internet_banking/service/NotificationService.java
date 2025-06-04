package com.wnc.internet_banking.service;

import com.wnc.internet_banking.dto.response.notification.NotificationDto;
import com.wnc.internet_banking.entity.User;

import java.util.List;
import java.util.UUID;

public interface NotificationService {
    List<NotificationDto> getAllNotificationsByUserId(UUID userId);

    void markAsRead(UUID userId, UUID notificationId);

    void markAllAsRead(UUID userId);

    void createNotification(UUID userId, String title, String content);
}