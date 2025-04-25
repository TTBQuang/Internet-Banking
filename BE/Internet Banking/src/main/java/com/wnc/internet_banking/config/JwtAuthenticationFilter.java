package com.wnc.internet_banking.config;

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
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

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
                UUID userId = jwtUtil.getUserIdFromToken(token);

                // Truy vấn database để lấy thông tin user
                Optional<User> userOptional = userRepository.findByUserId(userId);

                if (userOptional.isPresent()) {
                    User user = userOptional.get();

                    // Tạo danh sách quyền dựa trên role của user
                    List<GrantedAuthority> authorities = new ArrayList<>();
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

                    // Thêm các quyền cụ thể nếu cần
                    switch (user.getRole()) {
                        case ADMIN:
                            authorities.add(new SimpleGrantedAuthority("FULL_ACCESS"));
                            authorities.add(new SimpleGrantedAuthority("COMMENT"));
                            break;
                        case EMPLOYEE:
                            authorities.add(new SimpleGrantedAuthority("COMMENT"));
                            break;
                        case CUSTOMER:
                            authorities.add(new SimpleGrantedAuthority("COMMENT"));
                            break;
                    }

                    // Tạo UserDetails từ thông tin user
                    UserDetails userDetails =
                            org.springframework.security.core.userdetails.User.builder()
                                    .username(userId.toString())
                                    .password("")
                                    .authorities(authorities)
                                    .build();

                    // Tạo authentication token
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    authorities
                            );

                    // Thêm thông tin chi tiết về request hiện tại
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Cập nhật SecurityContext với authentication đã xác thực
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    // Không tìm thấy user
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    String json = new ObjectMapper().writeValueAsString(
                            Collections.singletonMap("message", "User not found")
                    );
                    response.getWriter().write(json);
                    return;
                }
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                String json = new ObjectMapper().writeValueAsString(
                        Collections.singletonMap("message", "Invalid or expired token")
                );
                response.getWriter().write(json);
                return;
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            String json = new ObjectMapper().writeValueAsString(
                    Collections.singletonMap("message", "Unauthorized")
            );
            response.getWriter().write(json);
            return;
        }
        filterChain.doFilter(request, response);
    }
}
