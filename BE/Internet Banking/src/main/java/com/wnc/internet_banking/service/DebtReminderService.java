package com.wnc.internet_banking.service;

import com.wnc.internet_banking.dto.request.debtreminder.CreateDebtReminderRequest;
import com.wnc.internet_banking.dto.response.debtreminder.DebtReminderDto;
import com.wnc.internet_banking.entity.Account;
import com.wnc.internet_banking.entity.DebtReminder;
import com.wnc.internet_banking.entity.User;
import com.wnc.internet_banking.repository.AccountRepository;
import com.wnc.internet_banking.repository.DebtReminderRepository;
import com.wnc.internet_banking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class DebtReminderService {

    private final DebtReminderRepository debtReminderRepository;

    private final UserRepository userRepository;

    private final AccountRepository accountRepository;

    private final ModelMapper modelMapper;


    public boolean isDebtReminderAlreadyPaid(UUID debtReminderId) {
        return debtReminderRepository.existsByDebtReminderIdAndStatus(debtReminderId, DebtReminder.Status.PAID);
    }

    public void confirmDebtPayment(UUID debtReminderID) {
        DebtReminder debtReminder = debtReminderRepository.findById(debtReminderID)
                .orElseThrow(() -> new IllegalArgumentException("Debt reminder not found"));

        debtReminder.setStatus(DebtReminder.Status.PAID);
        debtReminder.setPaidAt(LocalDateTime.now());
        debtReminderRepository.save(debtReminder);
    }

    public DebtReminderDto createDebtReminder(CreateDebtReminderRequest createDebtReminderRequest, UUID userId) {
        User creditor = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Account debtorAccount = accountRepository.findById(createDebtReminderRequest.getDebtorAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Debtor account not found"));

        // Create Debt reminder
        DebtReminder debtReminder = DebtReminder.builder()
                .creditor(creditor)
                .debtorAccount(debtorAccount)
                .amount(createDebtReminderRequest.getAmount())
                .content(createDebtReminderRequest.getContent())
                .status(DebtReminder.Status.PENDING)
                .build();

        debtReminderRepository.save(debtReminder);

        return modelMapper.map(debtReminder, DebtReminderDto.class);
    }

    public DebtReminderDto getDebtReminderById(UUID debtReminderId) {
        DebtReminder debtReminder = debtReminderRepository.findById(debtReminderId)
                .orElseThrow(() -> new IllegalArgumentException("Debt reminder not found"));

        return modelMapper.map(debtReminder, DebtReminderDto.class);
    }
}
