package com.wnc.internet_banking.repository;

import com.wnc.internet_banking.entity.Account;
import com.wnc.internet_banking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    Optional<Account> findByUser_UserId(UUID userId);

    Optional<Account> findByUser(User user);

    Optional<Account> findByAccountIdAndUser(UUID accountId, User user);

    Optional<Account> findByAccountNumber(String accountNumber);

    void deleteAllByUser_UserId(UUID userId);
}
