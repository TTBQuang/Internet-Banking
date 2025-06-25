package com.wnc.internet_banking.service.impl;

import com.wnc.internet_banking.dto.request.user.CustomerRegistrationDto;
import com.wnc.internet_banking.dto.request.user.EmployeeRegistrationDto;
import com.wnc.internet_banking.entity.User;
import com.wnc.internet_banking.repository.*;
import com.wnc.internet_banking.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service("userService")
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final OtpRepository otpRepository;

    private final NotificationRepository notificationRepository;

    private final RecipientRepository recipientRepository;

    private final DebtReminderRepository debtReminderRepository;

    private final AccountRepository accountRepository;

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
    @Override
    public Page<User> getAllCustomers(int page, int size) {
        return userRepository.findAllByRole(User.Role.CUSTOMER, PageRequest.of(page, size))
                .map(this::mapToCustomerResponse);
    }
    @Override
    public User updateCustomer(UUID userId, CustomerRegistrationDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return mapToCustomerResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteCustomer(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        if (user.getRole() != User.Role.CUSTOMER) {
            throw new IllegalArgumentException("Can only delete customers");
        }

        otpRepository.deleteAllByUser_UserId(userId);

        notificationRepository.deleteAllByUser_UserId(userId);

        recipientRepository.deleteAllByOwner_UserId(userId);

        debtReminderRepository.deleteAllByCreditor_UserId(userId);

        accountRepository.deleteAllByUser_UserId(userId);

        userRepository.deleteById(userId);
    }


    private User mapToCustomerResponse(User user) {
        User response = new User();
        response.setUserId(user.getUserId());
        response.setUsername(user.getUsername());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        return response;
    }

    @Override
    public Page<User> getAllEmployees(int page, int size) {
        return userRepository.findAllByRole(User.Role.EMPLOYEE, PageRequest.of(page, size))
                .map(this::mapToEmployeeResponse);
    }

    @Override
    public User updateEmployee(UUID userId, EmployeeRegistrationDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        return mapToEmployeeResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteEmployee(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));
        if (user.getRole() != User.Role.EMPLOYEE) {
            throw new IllegalArgumentException("Can only delete employees");
        }
        userRepository.deleteById(userId);
    }

    private User mapToEmployeeResponse(User user) {
        User response = new User();
        response.setUserId(user.getUserId());
        response.setUsername(user.getUsername());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        return response;
    }
}