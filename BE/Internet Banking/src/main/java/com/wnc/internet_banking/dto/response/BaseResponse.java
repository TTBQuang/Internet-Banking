package com.wnc.internet_banking.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse<T> {
    private String message;
    private T data;

    public static <T> BaseResponse<T> data(T data) {
        return new BaseResponse<>(null, data);
    }

    public static <T> BaseResponse<T> message(String message) {
        return new BaseResponse<>(message, null);
    }
}
