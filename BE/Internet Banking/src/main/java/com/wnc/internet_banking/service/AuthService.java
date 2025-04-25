package com.wnc.internet_banking.service;

import com.wnc.internet_banking.dto.response.auth.LoginResponse;
import com.wnc.internet_banking.entity.User;
import com.wnc.internet_banking.exception.InvalidCredentialsException;
import com.wnc.internet_banking.repository.UserRepository;
import com.wnc.internet_banking.util.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginResponse loginUser(String username, String rawPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidCredentialsException("Username not found"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid password");
        }

        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken();

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return new LoginResponse(accessToken, refreshToken);
    }

    public void logoutUser(UUID userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        user.setRefreshToken("");
        userRepository.save(user);
    }
}
