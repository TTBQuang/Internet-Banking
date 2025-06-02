package com.wnc.internet_banking.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wnc.internet_banking.entity.User;
import com.wnc.internet_banking.repository.UserRepository;
import com.wnc.internet_banking.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            if (jwtUtil.validateToken(token)) {
                // Get user id from db
                UUID userId = jwtUtil.getUserIdFromToken(token);
                Optional<User> userOptional = userRepository.findByUserId(userId);

                if (userOptional.isPresent()) {
                    User user = userOptional.get();

                    // Assign role
                    List<GrantedAuthority> authorities = List.of(
                            new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
                    );

                    UserDetails userDetails =
                            org.springframework.security.core.userdetails.User.builder()
                                    .username(userId.toString())
                                    .password("")
                                    .authorities(authorities)
                                    .build();

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    authorities
                            );

                    // Update SecurityContextHolder
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    returnErrorMessage(response, "User not found");
                    return;
                }
            } else {
                returnErrorMessage(response, "Invalid token");
                return;
            }
        } else {
            returnErrorMessage(response, "Unauthorized");
            return;
        }
        filterChain.doFilter(request, response);
    }

    private void returnErrorMessage(@NonNull HttpServletResponse response, String message) throws IOException{
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        String json = new ObjectMapper().writeValueAsString(
                Collections.singletonMap("message", message)
        );
        response.getWriter().write(json);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui")
                || path.equals("/auth/login")
                || path.equals("/auth/refresh")
                || path.equals("/auth/password-reset/initiate")
                || path.equals("/auth/password-reset/verify")
                || path.startsWith("/api/linked-banks");
    }
}
