package com.wnc.internet_banking.service;

import com.wnc.internet_banking.dto.request.auth.RecaptchaConfig;
import com.wnc.internet_banking.exception.InvalidRecaptchaException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class RecaptchaService {
    private final RecaptchaConfig recaptchaConfig;
    private final WebClient.Builder webClientBuilder;

    public boolean validateToken(String recaptchaToken) {
        String url = UriComponentsBuilder.fromUriString(recaptchaConfig.getVerifyUrl())
                .queryParam("secret", recaptchaConfig.getSecretKey())
                .queryParam("response", recaptchaToken)
                .build()
                .toUriString();

        Map<String, Object> response = webClientBuilder.build()
                .post()
                .uri(url)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

        if (response == null) {
            throw new InvalidRecaptchaException("Failed to get response from reCAPTCHA server");
        }

        return (boolean) response.get("success");
    }
}
