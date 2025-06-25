package com.wnc.internet_banking.service.impl;

import com.wnc.internet_banking.dto.response.notification.NotificationDto;
import com.wnc.internet_banking.entity.Notification;
import com.wnc.internet_banking.entity.User;
import com.wnc.internet_banking.repository.NotificationRepository;
import com.wnc.internet_banking.repository.UserRepository;
import com.wnc.internet_banking.service.NotificationService;
import com.wnc.internet_banking.controller.NotificationWebSocketController;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service("notificationService")
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final NotificationWebSocketController notificationWebSocketController;

    @Override
    public List<NotificationDto> getAllNotificationsByUserId(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<Notification> notifications = notificationRepository.findAllByUserOrderByCreatedAtDesc(user);

        return notifications.stream()
                .map(n -> modelMapper.map(n, NotificationDto.class))
                .toList();
    }

    @Override
    public void markAsRead(UUID userId, UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("Notification not found"));

        if (!notification.getUser().getUserId().equals(userId)) {
            throw new SecurityException("You are not allowed to modify this notification");
        }

        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    @Override
    public void markAllAsRead(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<Notification> notifications = notificationRepository.findAllByUserOrderByCreatedAtDesc(user);
        notifications.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(notifications);
    }

    @Override
    @Transactional
    public void createNotification(UUID userId, String title, String content) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setContent(content);

        notificationRepository.save(notification);

        // Gửi realtime notification qua websocket
        NotificationDto dto = modelMapper.map(notification, NotificationDto.class);
        notificationWebSocketController.sendNotificationToUser(userId.toString(), dto);
    }
}
