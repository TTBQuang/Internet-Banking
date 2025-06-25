package com.wnc.internet_banking.repository;

import com.wnc.internet_banking.entity.DebtReminder;
import com.wnc.internet_banking.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DebtReminderRepository extends JpaRepository<DebtReminder, UUID> {
    boolean existsByDebtReminderIdAndStatus(UUID debtReminderId, DebtReminder.Status status);

    Page<DebtReminder> findByCreditorOrDebtorAccount_User(User creditor, User debtor, Pageable pageable);

    // Search all debt reminders for a user, by keyword in content or creditor's full name or debtor's full name
    @Query("""
                SELECT d FROM DebtReminder d
                WHERE 
                    (d.creditor = :user OR d.debtorAccount.user = :user)
                    AND 
                    (
                        LOWER(d.content) LIKE LOWER(CONCAT('%', :keyword, '%'))
                        OR LOWER(d.creditor.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                        OR LOWER(d.debtorAccount.user.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    )
            """)
    Page<DebtReminder> searchByUserAndKeyword(
            @Param("user") User user,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    Page<DebtReminder> findByCreditor(User creditor, Pageable pageable);

    // Search sent debt reminders by creditor, by keyword in content or debtor's full name
    @Query("""
                SELECT d FROM DebtReminder d
                WHERE 
                    d.creditor = :user
                    AND (
                        LOWER(d.content) LIKE LOWER(CONCAT('%', :keyword, '%'))
                        OR LOWER(d.debtorAccount.user.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    )
            """)
    Page<DebtReminder> searchSentDebtReminders(
            @Param("user") User user,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    Page<DebtReminder> findByDebtorAccount_User(User debtor, Pageable pageable);

    // Search received debt reminders by debtor, by keyword in content or creditor's full name
    @Query("""
                SELECT d FROM DebtReminder d
                WHERE 
                    d.debtorAccount.user = :user
                    AND (
                        LOWER(d.content) LIKE LOWER(CONCAT('%', :keyword, '%'))
                        OR LOWER(d.creditor.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    )
            """)
    Page<DebtReminder> searchReceivedDebtReminders(
            @Param("user") User user,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    void deleteAllByCreditor_UserId(UUID userId);

    void deleteAllByDebtorAccount_User_UserId(UUID userId);
}

