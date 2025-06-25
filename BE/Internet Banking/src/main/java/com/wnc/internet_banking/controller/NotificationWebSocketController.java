package com.wnc.internet_banking.controller;

import com.wnc.internet_banking.dto.response.notification.NotificationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationWebSocketController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Gửi một thông báo đến người dùng qua WebSocket.
     *
     * @param userId ID của người dùng (String, thường là UUID hoặc username)
     * @param notification Thông báo cần gửi
     */
    public void sendNotificationToUser(String userId, NotificationDto notification) {
        messagingTemplate.convertAndSend("/topic/notifications/" + userId, notification);
    }
} 