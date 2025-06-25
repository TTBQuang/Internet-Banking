package com.wnc.internet_banking.service;

import com.wnc.internet_banking.dto.request.user.CustomerRegistrationDto;
import com.wnc.internet_banking.dto.request.user.EmployeeRegistrationDto;
import com.wnc.internet_banking.entity.User;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface UserService {

    User createEmployee(EmployeeRegistrationDto dto);

    User createCustomer(CustomerRegistrationDto dto);

    Page<User> getAllCustomers(int page, int size);

    User updateCustomer(UUID userId, CustomerRegistrationDto request);

    void deleteCustomer(UUID userId);

    Page<User> getAllEmployees(int page, int size);

    User updateEmployee(UUID userId, EmployeeRegistrationDto dto);

    void deleteEmployee(UUID userId);
}
