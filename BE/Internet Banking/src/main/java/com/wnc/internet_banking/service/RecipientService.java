package com.wnc.internet_banking.service;

import com.wnc.internet_banking.dto.request.recipient.RecipientCreateRequest;
import com.wnc.internet_banking.dto.request.recipient.RecipientUpdateRequest;
import com.wnc.internet_banking.dto.response.recipient.RecipientDto;
import com.wnc.internet_banking.entity.Account;
import com.wnc.internet_banking.entity.LinkedBank;
import com.wnc.internet_banking.entity.Recipient;
import com.wnc.internet_banking.entity.User;
import com.wnc.internet_banking.repository.AccountRepository;
import com.wnc.internet_banking.repository.LinkedBankRepository;
import com.wnc.internet_banking.repository.RecipientRepository;
import com.wnc.internet_banking.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private ModelMapper modelMapper;

    public RecipientDto addRecipient(UUID userId, RecipientCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean exists = recipientRepository.existsByOwnerUserIdAndAccountNumberAndBankLinkedBankId(
                userId, request.getAccountNumber(), request.getBankId()
        );
        if (exists) {
            throw new IllegalArgumentException("Recipient already exists");
        }

        Recipient recipient = new Recipient();
        recipient.setOwner(user);
        recipient.setAccountNumber(request.getAccountNumber());
        recipient.setCreatedAt(LocalDateTime.now());

        if (request.getBankId() == null) {
            Optional<User> recipientUser = accountRepository.findByAccountNumber(request.getAccountNumber())
                    .map(Account::getUser);
            if (recipientUser.isPresent()) {
                recipient.setNickname(request.getNickname() != null ? request.getNickname() : recipientUser.get().getFullName());
            } else {
                throw new IllegalArgumentException("Recipient user not found");
            }
        } else {
            LinkedBank bank = linkedBankRepository.findById(request.getBankId())
                    .orElseThrow(() -> new IllegalArgumentException("Bank not found"));
            recipient.setBank(bank);
            recipient.setNickname(request.getNickname() != null ? request.getNickname() : request.getFullName());
        }

        Recipient savedRecipient = recipientRepository.save(recipient);

        return modelMapper.map(savedRecipient, RecipientDto.class);
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

    public Page<RecipientDto> getRecipientsByUser(UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Recipient> recipients = recipientRepository.findByOwnerUserId(userId, pageable);
        return recipients.map(recipient -> modelMapper.map(recipient, RecipientDto.class));
    }

    @Transactional
    public RecipientDto updateRecipientNickname(UUID recipientId, RecipientUpdateRequest request) {
        Recipient recipient = recipientRepository.findById(recipientId)
                .orElseThrow(() -> new IllegalArgumentException("Recipient not found"));

        recipient.setNickname(request.getNickname());
        Recipient updatedRecipient = recipientRepository.save(recipient);

        return modelMapper.map(updatedRecipient, RecipientDto.class);
    }
}
