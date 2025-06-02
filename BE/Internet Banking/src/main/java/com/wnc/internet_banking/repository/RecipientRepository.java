package com.wnc.internet_banking.repository;

import com.wnc.internet_banking.entity.Recipient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RecipientRepository extends JpaRepository<Recipient, UUID> {
    boolean existsByOwnerUserIdAndAccountNumberAndBankLinkedBankId(UUID ownerId, String accountNumber, UUID bankId);
    Page<Recipient> findByOwner_UserId(UUID ownerUserId, Pageable pageable);
    Page<Recipient> findByOwner_UserIdAndNicknameContainingIgnoreCase(UUID ownerUserId, String nickname, Pageable pageable);
}

