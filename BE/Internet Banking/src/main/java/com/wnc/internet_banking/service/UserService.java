package com.wnc.internet_banking.service;

import com.wnc.internet_banking.dto.request.user.CustomerRegistrationDto;
import com.wnc.internet_banking.dto.request.user.EmployeeRegistrationDto;
import com.wnc.internet_banking.dto.response.BaseResponse;
import com.wnc.internet_banking.dto.response.user.UserDto;
import com.wnc.internet_banking.entity.User;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface UserService {

    BaseResponse<User> createEmployee(EmployeeRegistrationDto dto);

    BaseResponse<User> createCustomer(CustomerRegistrationDto dto);

    Page<UserDto> getAllCustomers(int page, int size);

    User updateCustomer(UUID userId, CustomerRegistrationDto request);

    void deleteCustomer(UUID userId);

    Page<UserDto> getAllEmployees(int page, int size);

    BaseResponse<User> updateEmployee(UUID userId, EmployeeRegistrationDto dto);

    BaseResponse<Boolean> deleteEmployee(UUID userId);
}
