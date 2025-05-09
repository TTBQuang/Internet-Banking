package com.wnc.internet_banking.service;

import com.wnc.internet_banking.dto.response.auth.LoginResponse;
import com.wnc.internet_banking.dto.response.auth.TokenResponse;
import com.wnc.internet_banking.dto.response.user.UserDto;
import com.wnc.internet_banking.entity.Account;
import com.wnc.internet_banking.entity.Otp;
import com.wnc.internet_banking.entity.Transaction;
import com.wnc.internet_banking.entity.User;
import com.wnc.internet_banking.exception.InvalidCredentialsException;
import com.wnc.internet_banking.exception.InvalidRecaptchaException;
import com.wnc.internet_banking.repository.UserRepository;
import com.wnc.internet_banking.util.EmailTemplate;
import com.wnc.internet_banking.util.JwtUtil;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final ModelMapper modelMapper;
    private final RecaptchaService recaptchaService;
    private final OtpService otpService;
    private final EmailService emailService;

    public LoginResponse loginUser(String username, String rawPassword, String recaptchaToken) {
        // Validate reCAPTCHA token
        if (!recaptchaService.validateToken(recaptchaToken)) {
            throw new InvalidRecaptchaException("Invalid reCAPTCHA verification");
        }

        // Authenticate user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidCredentialsException("Username not found"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid password");
        }

        // Generate JWT tokens
        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken();

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        UserDto userDto = modelMapper.map(user, UserDto.class);
        TokenResponse tokenResponse = new TokenResponse(accessToken, refreshToken);

        return new LoginResponse(tokenResponse, userDto);
    }

    public void logoutUser(UUID userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        user.setRefreshToken("");
        userRepository.save(user);
    }

    public void changePassword(UUID userId, String oldPassword, String newPassword) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new InvalidCredentialsException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public TokenResponse refreshToken(String refreshToken) {
        User user = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid refresh token"));

        String newAccessToken = jwtUtil.generateAccessToken(user);
        String newRefreshToken = jwtUtil.generateRefreshToken();

        user.setRefreshToken(newRefreshToken);
        userRepository.save(user);

        return new TokenResponse(newAccessToken, newRefreshToken);
    }

    @Transactional
    public UUID initiatePasswordReset(String email) {
        User sender = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Send OTP to sender's email
        Otp otp = otpService.generateOtp(sender.getUserId(), Otp.Purpose.PASSWORD_RESET);
        try {
            String emailBody = EmailTemplate.passwordReset(sender.getFullName(), otp.getOtpCode());
            emailService.sendEmail(sender.getEmail(), "Password Reset Request", emailBody);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email");
        }

        return sender.getUserId();
    }

    @Transactional
    public void verifyPasswordReset(UUID userId, String otpCode, String newPassword) {
        // Check otp
        if(!otpService.verifyOtp(userId, otpCode, Otp.Purpose.PASSWORD_RESET)) {
            throw new IllegalArgumentException("Invalid OTP");
        }

        // If the OTP is correct, proceed to reset the password
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }
}
