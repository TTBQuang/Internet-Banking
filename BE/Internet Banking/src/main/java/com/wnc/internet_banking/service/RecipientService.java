package com.wnc.internet_banking.service;

import com.wnc.internet_banking.dto.request.recipient.RecipientCreateRequest;
import com.wnc.internet_banking.dto.request.recipient.RecipientUpdateRequest;
import com.wnc.internet_banking.dto.response.recipient.RecipientDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface RecipientService {
    RecipientDto addRecipient(UUID userId, RecipientCreateRequest request);

    void deleteRecipient(UUID recipientId);

    boolean isRecipientOwner(UUID recipientId, String userId);

    Page<RecipientDto> getRecipientsByUserAndNickname(UUID userId, String nickname, int page, int size);

    List<RecipientDto> getRecipientsByUser(UUID userId);

    List<RecipientDto> getAllInternalRecipientsByUser(UUID userId);

    List<RecipientDto> getAllInternalRecipientsByUserAndNickname(UUID userId, String nickname);

    RecipientDto updateRecipientNickname(UUID recipientId, RecipientUpdateRequest request);
}
