package com.wnc.internet_banking.service;

import com.wnc.internet_banking.entity.Otp;

import java.util.UUID;

public interface OtpService {
    Otp generateOtp(UUID userId, Otp.Purpose purpose);

    boolean verifyOtp(UUID userId, String otpCode, Otp.Purpose purpose);
}
