package com.wnc.internet_banking.service.impl;

import com.wnc.internet_banking.dto.request.user.CustomerRegistrationDto;
import com.wnc.internet_banking.dto.request.user.EmployeeRegistrationDto;
import com.wnc.internet_banking.dto.response.BaseResponse;
import com.wnc.internet_banking.dto.response.user.UserDto;
import com.wnc.internet_banking.entity.User;
import com.wnc.internet_banking.repository.*;
import com.wnc.internet_banking.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
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

    private final ModelMapper modelMapper;

//    @Override
//    public User createEmployee(EmployeeRegistrationDto dto) {
//        User employee = new User();
//        employee.setUsername(dto.getUsername());
//        employee.setPassword(passwordEncoder.encode(dto.getPassword()));
//        employee.setFullName(dto.getFullName());
//        employee.setEmail(dto.getEmail());
//        employee.setPhone(dto.getPhone());
//        employee.setRole(User.Role.EMPLOYEE);
//        employee.setRefreshToken(""); // Set an initial value for refresh token
//        return userRepository.save(employee);
//    }

    @Override
    public BaseResponse<User> createEmployee(EmployeeRegistrationDto dto) {
        try {
            Optional<User> existingUserByUsername = userRepository.findByUsername(dto.getUsername());
            if (existingUserByUsername.isPresent()) {
                return BaseResponse.message("Username đã tồn tại");
            }

            // Kiểm tra xem email đã tồn tại chưa
            Optional<User> existingUserByEmail = userRepository.findByEmail(dto.getEmail());
            if (existingUserByEmail.isPresent()) {
                return BaseResponse.message("Email đã tồn tại");
            }

            // Tạo customer user
            User employee = new User();
            employee.setUsername(dto.getUsername());
            employee.setPassword(passwordEncoder.encode(dto.getPassword()));
            employee.setFullName(dto.getFullName());
            employee.setEmail(dto.getEmail());
            employee.setPhone(dto.getPhone());
            employee.setRole(User.Role.EMPLOYEE);
            employee.setRefreshToken("");
            User savedEmployee = userRepository.save(employee);
            return BaseResponse.data(savedEmployee);
        } catch (Exception e) {
            return BaseResponse.message("Tạo nhân viên thất bại");
        }
    }

    @Override
    public BaseResponse<User> createCustomer(CustomerRegistrationDto dto) {
        try {
            // Kiểm tra xem username đã tồn tại chưa
            Optional<User> existingUserByUsername = userRepository.findByUsername(dto.getUsername());
            if (existingUserByUsername.isPresent()) {
                return BaseResponse.message("Username đã tồn tại");
            }

            // Kiểm tra xem email đã tồn tại chưa
            Optional<User> existingUserByEmail = userRepository.findByEmail(dto.getEmail());
            if (existingUserByEmail.isPresent()) {
                return BaseResponse.message("Email đã tồn tại");
            }

            // Tạo customer user
            User customer = new User();
            customer.setUsername(dto.getUsername());
            customer.setPassword(passwordEncoder.encode(dto.getPassword()));
            customer.setFullName(dto.getFullName());
            customer.setEmail(dto.getEmail());
            customer.setPhone(dto.getPhone());
            customer.setRole(User.Role.CUSTOMER);
            customer.setRefreshToken(""); // Set an initial value for refresh token
            User savedCustomer = userRepository.save(customer);

            // Tạo tài khoản thanh toán cho customer
            accountService.createAccountForUser(savedCustomer);

            return BaseResponse.data(savedCustomer);
        } catch (Exception e) {
            return BaseResponse.message("Tạo khách hàng thất bại");
        }
    }

    @Override
    public Page<UserDto> getAllCustomers(int page, int size) {
        return userRepository.findAllByRole(User.Role.CUSTOMER, PageRequest.of(page, size))
                .map(user -> modelMapper.map(user, UserDto.class));
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

        debtReminderRepository.deleteAllByDebtorAccount_User_UserId(userId);

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
    public Page<UserDto> getAllEmployees(int page, int size) {
        return userRepository.findAllByRole(User.Role.EMPLOYEE, PageRequest.of(page, size))
                .map(user -> modelMapper.map(user, UserDto.class));
    }

    @Override
    public BaseResponse<User> updateEmployee(UUID userId, EmployeeRegistrationDto dto) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("Employee not found"));
            user.setFullName(dto.getFullName());
            user.setEmail(dto.getEmail());
            user.setPhone(dto.getPhone());
            if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(dto.getPassword()));
            }
            userRepository.save(user);
            return BaseResponse.message("success");
        } catch (Exception e) {
            return BaseResponse.message("failed");
        }
    }

    @Override
    @Transactional
    public BaseResponse<Boolean> deleteEmployee(UUID userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("Employee not found"));
            if (user.getRole() != User.Role.EMPLOYEE) {
                throw new IllegalArgumentException("Can only delete employees");
            }
            userRepository.deleteById(userId);
            return BaseResponse.message("success");
        } catch (Exception e) {
            return BaseResponse.message("failed");
        }
    }
}