package com.wnc.internet_banking.repository;

import com.wnc.internet_banking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    Optional<User> findByUserId(UUID userId);
    Optional<User> findByRefreshToken(String refreshToken);
    Optional<User> findByEmail(String email);
}

