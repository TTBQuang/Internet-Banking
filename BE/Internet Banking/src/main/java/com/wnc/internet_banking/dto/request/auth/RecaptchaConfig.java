package com.wnc.internet_banking.dto.request.auth;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "google.recaptcha")
@Data
public class RecaptchaConfig {
    private String secretKey;
    private String verifyUrl = "https://www.google.com/recaptcha/api/siteverify";
}
