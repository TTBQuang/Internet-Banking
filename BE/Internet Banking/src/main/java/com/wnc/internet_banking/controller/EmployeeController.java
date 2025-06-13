package com.wnc.internet_banking.controller;

import com.wnc.internet_banking.dto.request.user.EmployeeRegistrationDto;
import com.wnc.internet_banking.dto.response.BaseResponse;
import com.wnc.internet_banking.entity.User;
import com.wnc.internet_banking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<BaseResponse<User>> registerEmployee(@RequestBody EmployeeRegistrationDto dto) {
        User employee = userService.createEmployee(dto);
        return ResponseEntity.ok(BaseResponse.data(employee));
    }
}