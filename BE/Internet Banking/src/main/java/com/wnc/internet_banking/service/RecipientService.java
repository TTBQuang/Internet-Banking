package com.wnc.internet_banking.service;

import com.wnc.internet_banking.dto.request.recipient.RecipientCreateRequest;
import com.wnc.internet_banking.entity.LinkedBank;
import com.wnc.internet_banking.entity.Recipient;
import com.wnc.internet_banking.entity.User;
import com.wnc.internet_banking.repository.LinkedBankRepository;
import com.wnc.internet_banking.repository.RecipientRepository;
import com.wnc.internet_banking.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RecipientService {
    private final RecipientRepository recipientRepository;
    private final UserRepository userRepository;
    private final LinkedBankRepository linkedBankRepository;

    public Recipient addRecipient(UUID userId, RecipientCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        LinkedBank bank = linkedBankRepository.findByBankCode(request.getBankCode())
                .orElseThrow(() -> new IllegalArgumentException("Bank not found"));

        Recipient recipient = new Recipient();
        recipient.setRecipientId(UUID.randomUUID());
        recipient.setOwner(user);
        recipient.setAccountNumber(request.getAccountNumber());
        recipient.setNickname(request.getNickname() != null ? request.getNickname() : user.getUsername());
        recipient.setBank(bank);
        recipient.setCreatedAt(LocalDateTime.now());

        return recipientRepository.save(recipient);
    }
}
