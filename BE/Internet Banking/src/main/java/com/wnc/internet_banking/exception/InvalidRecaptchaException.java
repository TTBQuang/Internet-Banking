package com.wnc.internet_banking.exception;

public class InvalidRecaptchaException extends RuntimeException {
    public InvalidRecaptchaException(String message) {
        super(message);
    }
}
