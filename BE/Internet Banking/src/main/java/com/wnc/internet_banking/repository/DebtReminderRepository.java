package com.wnc.internet_banking.repository;

import com.wnc.internet_banking.entity.DebtReminder;
import com.wnc.internet_banking.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DebtReminderRepository extends JpaRepository<DebtReminder, UUID> {
    boolean existsByDebtReminderIdAndStatus(UUID debtReminderId, DebtReminder.Status status);

    Page<DebtReminder> findByCreditor(User creditor, Pageable pageable);

    Page<DebtReminder> findByDebtorAccount_User(User debtor, Pageable pageable);
}

