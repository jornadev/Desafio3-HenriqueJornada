package com.compass.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class FeignClientException extends RuntimeException {
    public FeignClientException(String message) {
        super(message);
    }
}

