package com.wnc.internet_banking.service;

import com.wnc.internet_banking.dto.request.recipient.RecipientCreateRequest;
import com.wnc.internet_banking.entity.Account;
import com.wnc.internet_banking.entity.LinkedBank;
import com.wnc.internet_banking.entity.Recipient;
import com.wnc.internet_banking.entity.User;
import com.wnc.internet_banking.repository.AccountRepository;
import com.wnc.internet_banking.repository.LinkedBankRepository;
import com.wnc.internet_banking.repository.RecipientRepository;
import com.wnc.internet_banking.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RecipientService {
    private final RecipientRepository recipientRepository;
    private final UserRepository userRepository;
    private final LinkedBankRepository linkedBankRepository;
    private final AccountRepository accountRepository;

    public Recipient addRecipient(UUID userId, RecipientCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean exists = recipientRepository.existsByOwnerUserIdAndAccountNumberAndBankBankCode(
                userId, request.getAccountNumber(), request.getBankCode()
        );
        if (exists) {
            throw new IllegalArgumentException("Recipient already exists");
        }

        Recipient recipient = new Recipient();
        recipient.setOwner(user);
        recipient.setAccountNumber(request.getAccountNumber());
        recipient.setCreatedAt(LocalDateTime.now());

        if (request.getBankCode() == null) {
            Optional<User> recipientUser = accountRepository.findByAccountNumber(request.getAccountNumber())
                    .map(Account::getUser);
            if (recipientUser.isPresent()) {
                recipient.setNickname(request.getNickname() != null ? request.getNickname() : recipientUser.get().getFullName());
            } else {
                throw new IllegalArgumentException("Recipient user not found");
            }
        } else {
            LinkedBank bank = linkedBankRepository.findByBankCode(request.getBankCode())
                    .orElseThrow(() -> new IllegalArgumentException("Bank not found"));
            recipient.setBank(bank);
            // TODO: Call API to get username of recipient if nickname is not provided
            recipient.setNickname(request.getNickname() != null ? request.getNickname() : "TODO");
        }

        return recipientRepository.save(recipient);
    }

    @Transactional
    public void deleteRecipient(UUID recipientId) {
        Recipient recipient = recipientRepository.findById(recipientId)
                .orElseThrow(() -> new IllegalArgumentException("Recipient not found"));
        recipientRepository.delete(recipient);
    }

    public boolean isRecipientOwner(UUID recipientId, String userId) {
        return recipientRepository.findById(recipientId)
                .map(recipient -> userId.equals(recipient.getOwner().getUserId().toString()))
                .orElse(false);
    }
}
