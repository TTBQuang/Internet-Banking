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
import com.wnc.internet_banking.service.NotificationService;
import com.wnc.internet_banking.util.EmailTemplate;
import com.wnc.internet_banking.util.NotificationTemplate;
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

    private final NotificationService notificationService;

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

        // Notify to the creditor
        NotificationTemplate notification = NotificationTemplate.debtPaymentCompleted(debtReminder.getDebtorAccount().getUser().getFullName(),
                debtReminder.getAmount());

        notificationService.createNotification(debtReminder.getCreditor().getUserId(), notification.getTitle(), notification.getContent());
    }

    @Override
    public DebtReminderDto createDebtReminder(CreateDebtReminderRequest createDebtReminderRequest, UUID userId) {
        User creditor = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Account debtorAccount = accountRepository.findByAccountNumber(createDebtReminderRequest.getDebtorAccountNumber())
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

        // Notify to the debtor
        NotificationTemplate notification = NotificationTemplate.debtReminderCreated(
                creditor.getFullName(),
                debtReminder.getAmount()
        );

        notificationService.createNotification(
                debtorAccount.getUser().getUserId(),
                notification.getTitle(),
                notification.getContent());

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
    public Page<DebtReminderDto> getAllDebtRemindersByUser(UUID userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<DebtReminder> debtReminders = debtReminderRepository.findByCreditorOrDebtorAccount_User(user, user, pageable);

        return debtReminders.map(debtReminder -> modelMapper.map(debtReminder, DebtReminderDto.class));
    }

    @Override
    public Page<DebtReminderDto> searchAllDebtRemindersByUser(UUID userId, String query, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<DebtReminder> debtReminders = debtReminderRepository.searchByUserAndKeyword(user, query, pageable);

        return debtReminders.map(debtReminder -> modelMapper.map(debtReminder, DebtReminderDto.class));
    }

    @Override
    public Page<DebtReminderDto> getSentDebtRemindersByUser(UUID userId, int page, int size) {
        User creditor = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<DebtReminder> debtReminders = debtReminderRepository.findByCreditor(creditor, pageable);

        return debtReminders.map(debtReminder -> modelMapper.map(debtReminder, DebtReminderDto.class));
    }

    @Override
    public Page<DebtReminderDto> searchSentDebtRemindersByUser(UUID userId, String query, int page, int size) {
        User creditor = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<DebtReminder> debtReminders = debtReminderRepository.searchSentDebtReminders(creditor, query, pageable);

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

    @Override
    public Page<DebtReminderDto> searchReceivedDebtRemindersByUser(UUID userId, String query, int page, int size) {
        User debtor = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<DebtReminder> debtReminders = debtReminderRepository.searchReceivedDebtReminders(debtor, query, pageable);

        return debtReminders.map(debtReminder -> modelMapper.map(debtReminder, DebtReminderDto.class));
    }


    @Transactional
    @Override
    public void cancelDebtReminder(UUID debtReminderId, CancelDebtReminderRequest cancelDebtReminderRequest) {
        DebtReminder debtReminder = debtReminderRepository.findById(debtReminderId)
                .orElseThrow(() -> new IllegalArgumentException("Debt reminder not found"));

        debtReminderRepository.delete(debtReminder);

        // Notify to the debtor
        NotificationTemplate notification = NotificationTemplate.debtReminderCancelledToDebtor(
                debtReminder.getCreditor().getFullName(),
                cancelDebtReminderRequest.getContent()
        );

        notificationService.createNotification(
                debtReminder.getDebtorAccount().getUser().getUserId(),
                notification.getTitle(),
                notification.getContent());
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

        // Notify to the creditor
        NotificationTemplate notification = NotificationTemplate.debtReminderCancelledToCreditor(
                debtReminder.getDebtorAccount().getUser().getFullName(),
                cancelDebtReminderRequest.getContent()
        );

        notificationService.createNotification(
                debtReminder.getCreditor().getUserId(),
                notification.getTitle(),
                notification.getContent());
    }
}
