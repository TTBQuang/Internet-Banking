package com.wnc.internet_banking.repository;

import com.wnc.internet_banking.entity.Otp;
import com.wnc.internet_banking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OtpRepository extends JpaRepository<Otp, UUID> {
    @Modifying
    @Query("UPDATE Otp o SET o.isUsed = true WHERE o.user.userId = :userId AND o.purpose = :purpose AND o.isUsed = false AND o.expiredAt > :now")
    void deactivateActiveOtps(@Param("userId") UUID userId,
                              @Param("purpose") Otp.Purpose purpose,
                              @Param("now") LocalDateTime now);

    Optional<Otp> findByUserAndOtpCodeAndPurposeAndIsUsedFalse(User user, String otpCode, Otp.Purpose purpose);
}
