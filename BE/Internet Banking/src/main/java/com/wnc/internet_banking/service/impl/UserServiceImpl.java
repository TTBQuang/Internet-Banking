package com.wnc.internet_banking.service.impl;

import com.wnc.internet_banking.dto.request.user.CustomerRegistrationDto;
import com.wnc.internet_banking.dto.request.user.EmployeeRegistrationDto;
import com.wnc.internet_banking.entity.User;
import com.wnc.internet_banking.repository.UserRepository;
import com.wnc.internet_banking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountServiceImpl accountService;

    @Override
    public User createEmployee(EmployeeRegistrationDto dto) {
        User employee = new User();
        employee.setUsername(dto.getUsername());
        employee.setPassword(passwordEncoder.encode(dto.getPassword()));
        employee.setFullName(dto.getFullName());
        employee.setEmail(dto.getEmail());
        employee.setPhone(dto.getPhone());
        employee.setRole(User.Role.EMPLOYEE);
        employee.setRefreshToken(""); // Set an initial value for refresh token
        return userRepository.save(employee);
    }

    @Override
    public User createCustomer(CustomerRegistrationDto dto) {
        // Create customer user
        User customer = new User();
        customer.setUsername(dto.getUsername());
        customer.setPassword(passwordEncoder.encode(dto.getPassword()));
        customer.setFullName(dto.getFullName());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setRole(User.Role.CUSTOMER);
        customer.setRefreshToken(""); // Set an initial value for refresh token
        User savedCustomer = userRepository.save(customer);

        // Create payment account for the customer
        accountService.createAccountForUser(savedCustomer);

        return savedCustomer;
    }

}