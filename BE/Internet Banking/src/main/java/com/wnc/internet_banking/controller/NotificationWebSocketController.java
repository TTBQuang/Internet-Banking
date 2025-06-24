package com.wnc.internet_banking.controller;

import com.wnc.internet_banking.dto.response.notification.NotificationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationWebSocketController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendNotificationToUser(String userId, NotificationDto notification) {
        messagingTemplate.convertAndSend("/topic/notifications/" + userId, notification);
    }
} 