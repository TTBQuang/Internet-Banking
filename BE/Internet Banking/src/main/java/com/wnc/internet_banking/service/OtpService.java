package com.wnc.internet_banking.service;

import com.wnc.internet_banking.entity.Otp;
import com.wnc.internet_banking.entity.User;
import com.wnc.internet_banking.repository.OtpRepository;
import com.wnc.internet_banking.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class OtpService {

    private final OtpRepository otpRepository;

    private final UserRepository userRepository;

    public Otp generateOtp(UUID userId, Otp.Purpose purpose) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        // Deactivate any existing OTP with the same purpose for the user
        otpRepository.deactivateActiveOtps(userId, purpose, LocalDateTime.now());

        String otpCode = String.valueOf((int) (Math.random() * 900000) + 100000);
        LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(5);

        Otp otp = new Otp();
        otp.setUser(user);
        otp.setOtpCode(otpCode);
        otp.setPurpose(purpose);
        otp.setExpiredAt(expiredAt);
        otp.setUsed(false);

        return otpRepository.save(otp);
    }

    public boolean verifyOtp(UUID userId, String otpCode, Otp.Purpose purpose) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Otp otp = otpRepository.findByUserAndOtpCodeAndPurposeAndIsUsedFalse(user, otpCode, purpose)
                .orElseThrow(() -> new IllegalArgumentException("Invalid OTP"));

        if (otp.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("OTP has expired");
        }

        otp.setUsed(true);
        otpRepository.save(otp);

        return true;
    }
}
