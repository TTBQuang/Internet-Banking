package com.wnc.internet_banking.service;

public interface RecaptchaService {
    boolean validateToken(String recaptchaToken);
}
