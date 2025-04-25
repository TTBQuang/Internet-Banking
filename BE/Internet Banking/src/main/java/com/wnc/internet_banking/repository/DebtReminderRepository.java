package com.wnc.internet_banking.repository;

import com.wnc.internet_banking.entity.DebtReminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DebtReminderRepository extends JpaRepository<DebtReminder, UUID> {
}

