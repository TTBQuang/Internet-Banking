package com.wnc.internet_banking.controller;

import com.wnc.internet_banking.dto.request.auth.ChangePasswordRequest;
import com.wnc.internet_banking.dto.request.auth.LoginRequest;
import com.wnc.internet_banking.dto.response.auth.LoginResponse;
import com.wnc.internet_banking.service.AuthService;
import com.wnc.internet_banking.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginRequest request) {
        LoginResponse response = authService.loginUser(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser() {
        // Lấy thông tin authentication từ SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            // Lấy userId từ principal (username)
            String userIdString = authentication.getName();
            UUID userId = UUID.fromString(userIdString);

            // Đăng xuất user
            authService.logoutUser(userId);

            // Xóa context authentication
            SecurityContextHolder.clearContext();

            return ResponseEntity.ok("Logged out successfully");
        } else {
            throw new AccessDeniedException("Unauthorized");
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            System.out.println("User is authenticated");
            UUID userId = UUID.fromString(authentication.getName());
            authService.changePassword(userId, request.getOldPassword(), request.getNewPassword());
            return ResponseEntity.ok("Password changed successfully");
        } else {
            throw new AccessDeniedException("Unauthorized");
        }
    }
}
