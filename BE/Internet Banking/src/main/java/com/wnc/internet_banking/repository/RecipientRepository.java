package com.wnc.internet_banking.repository;

import com.wnc.internet_banking.entity.Recipient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RecipientRepository extends JpaRepository<Recipient, UUID> {
    boolean existsByOwnerUserIdAndAccountNumberAndBankBankCode(UUID ownerId, String accountNumber, String bankCode);
}

