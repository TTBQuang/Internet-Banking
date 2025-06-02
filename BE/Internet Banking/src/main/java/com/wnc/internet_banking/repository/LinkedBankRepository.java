package com.wnc.internet_banking.repository;

import com.wnc.internet_banking.entity.LinkedBank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LinkedBankRepository extends JpaRepository<LinkedBank, UUID> {
    Optional<LinkedBank> findByBankCode(String bankCode);
}

