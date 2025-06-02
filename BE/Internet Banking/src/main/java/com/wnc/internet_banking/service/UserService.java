package com.wnc.internet_banking.service;

import com.wnc.internet_banking.dto.request.user.CustomerRegistrationDto;
import com.wnc.internet_banking.dto.request.user.EmployeeRegistrationDto;
import com.wnc.internet_banking.entity.User;

public interface UserService {

    User createEmployee(EmployeeRegistrationDto dto);

    User createCustomer(CustomerRegistrationDto dto);

}
