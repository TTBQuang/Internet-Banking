package com.wnc.internet_banking.dto.response.notification;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class NotificationDto {
    private UUID notificationId;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private boolean isRead;
}

