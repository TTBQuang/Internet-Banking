package com.wnc.internet_banking.repository;

import com.wnc.internet_banking.entity.Notification;
import com.wnc.internet_banking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findAllByUserOrderByCreatedAtDesc(User user);

    void deleteAllByUser_UserId(UUID userId);
}
