package com.wnc.internet_banking.repository;

import com.wnc.internet_banking.entity.Account;
import com.wnc.internet_banking.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

}
