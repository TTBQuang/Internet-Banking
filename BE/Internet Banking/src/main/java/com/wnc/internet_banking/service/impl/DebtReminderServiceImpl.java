package com.wnc.internet_banking.service.impl;

import com.wnc.internet_banking.dto.request.debtreminder.CancelDebtReminderRequest;
import com.wnc.internet_banking.dto.request.debtreminder.CreateDebtReminderRequest;
import com.wnc.internet_banking.dto.response.debtreminder.DebtReminderDto;
import com.wnc.internet_banking.entity.Account;
import com.wnc.internet_banking.entity.DebtReminder;
import com.wnc.internet_banking.entity.User;
import com.wnc.internet_banking.repository.AccountRepository;
import com.wnc.internet_banking.repository.DebtReminderRepository;
import com.wnc.internet_banking.repository.UserRepository;
import com.wnc.internet_banking.service.DebtReminderService;
import com.wnc.internet_banking.service.EmailService;
import com.wnc.internet_banking.util.EmailTemplate;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Service("debtReminderService")
public class DebtReminderServiceImpl implements DebtReminderService {

    private final EmailService emailService;

    private final DebtReminderRepository debtReminderRepository;

    private final UserRepository userRepository;

    private final AccountRepository accountRepository;

    private final ModelMapper modelMapper;


    @Override
    public boolean isDebtReminderAlreadyPaid(UUID debtReminderId) {
        return debtReminderRepository.existsByDebtReminderIdAndStatus(debtReminderId, DebtReminder.Status.PAID);
    }

    @Transactional
    @Override
    public void confirmDebtPayment(UUID debtReminderID) {
        DebtReminder debtReminder = debtReminderRepository.findById(debtReminderID)
                .orElseThrow(() -> new IllegalArgumentException("Debt reminder not found"));

        debtReminder.setStatus(DebtReminder.Status.PAID);
        debtReminder.setPaidAt(LocalDateTime.now());
        debtReminderRepository.save(debtReminder);

        // Send notify email to the creditor
        try {
            String emailBody = EmailTemplate.notifyDebtPaymentCompleted(
                    debtReminder.getCreditor().getFullName(),
                    debtReminder.getDebtorAccount().getUser().getFullName(),
                    debtReminder.getAmount());
            emailService.sendEmail(debtReminder.getCreditor().getEmail(), "Debt Payment Completed", emailBody);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email");
        }
    }

    @Override
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

    @Override
    public boolean isDebtReminderOwner(UUID debtReminderId, String userId) {
        DebtReminder debtReminder = debtReminderRepository.findById(debtReminderId)
                .orElseThrow(() -> new IllegalArgumentException("Debt reminder not found"));

        return debtReminder.getCreditor().getUserId().toString().equals(userId);
    }

    @Override
    public DebtReminderDto getDebtReminderById(UUID debtReminderId) {
        DebtReminder debtReminder = debtReminderRepository.findById(debtReminderId)
                .orElseThrow(() -> new IllegalArgumentException("Debt reminder not found"));

        return modelMapper.map(debtReminder, DebtReminderDto.class);
    }

    @Override
    public Page<DebtReminderDto> getDebtRemindersByUser(UUID userId, int page, int size) {
        User creditor = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<DebtReminder> debtReminders = debtReminderRepository.findByCreditor(creditor, pageable);

        return debtReminders.map(debtReminder -> modelMapper.map(debtReminder, DebtReminderDto.class));
    }

    @Override
    public Page<DebtReminderDto> getReceivedDebtRemindersByUser(UUID userId, int page, int size) {
        User debtor = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<DebtReminder> debtReminders = debtReminderRepository.findByDebtorAccount_User(debtor, pageable);

        return debtReminders.map(debtReminder -> modelMapper.map(debtReminder, DebtReminderDto.class));
    }

    @Transactional
    @Override
    public void cancelDebtReminder(UUID debtReminderId, CancelDebtReminderRequest cancelDebtReminderRequest) {
        DebtReminder debtReminder = debtReminderRepository.findById(debtReminderId)
                .orElseThrow(() -> new IllegalArgumentException("Debt reminder not found"));

        debtReminderRepository.delete(debtReminder);

        // Send notify email to the debtor
        try {
            String emailBody = EmailTemplate.notifyDebtReminderCancelledToDebtor(
                    debtReminder.getDebtorAccount().getUser().getFullName(),
                    debtReminder.getCreditor().getFullName(),
                    cancelDebtReminderRequest.getContent()
            );
            emailService.sendEmail(debtReminder.getDebtorAccount().getUser().getEmail(), "Debt Reminder Cancelled", emailBody);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email");
        }

    }

    @Transactional
    @Override
    public void cancelReceivedDebtReminder(UUID debtReminderId, CancelDebtReminderRequest cancelDebtReminderRequest, UUID userId) {
        DebtReminder debtReminder = debtReminderRepository.findById(debtReminderId)
                .orElseThrow(() -> new IllegalArgumentException("Debt reminder not found"));

        // Check if the user is the debtor
        User debtor = debtReminder.getDebtorAccount().getUser();
        if (!debtor.getUserId().equals(userId)) {
            throw new IllegalArgumentException("You are not the debtor of this debt reminder");
        }

        debtReminderRepository.delete(debtReminder);

        // Send notify email to the creditor
        try {
            String emailBody = EmailTemplate.notifyDebtReminderCancelledToCreditor(
                    debtReminder.getCreditor().getFullName(),
                    debtor.getFullName(),
                    cancelDebtReminderRequest.getContent()
            );
            emailService.sendEmail(debtReminder.getCreditor().getEmail(), "Debt Reminder Cancelled", emailBody);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email");
        }
    }
}
