package com.wnc.internet_banking.controller;

import com.wnc.internet_banking.dto.request.user.EmployeeRegistrationDto;
import com.wnc.internet_banking.entity.User;
import com.wnc.internet_banking.dto.response.BaseResponse;
import com.wnc.internet_banking.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/employees")
@Tag(name = "Employee API", description = "Quản lý nhân viên")
public class EmployeeController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<Page<User>>> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<User> employees = userService.getAllEmployees(page, size);
        return ResponseEntity.ok(BaseResponse.data(employees));
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<User>> registerEmployee(@RequestBody EmployeeRegistrationDto dto) {
        User employee = userService.createEmployee(dto);
        return ResponseEntity.ok(BaseResponse.data(employee));
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<User>> updateEmployee(
            @PathVariable UUID userId,
            @RequestBody EmployeeRegistrationDto dto) {
        User employee = userService.updateEmployee(userId, dto);
        return ResponseEntity.ok(BaseResponse.data(employee));
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEmployee(@PathVariable UUID userId) {
        userService.deleteEmployee(userId);
        return ResponseEntity.noContent().build();
    }
}