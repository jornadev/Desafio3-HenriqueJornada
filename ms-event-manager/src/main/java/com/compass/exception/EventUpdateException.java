package com.compass.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EventUpdateException extends RuntimeException {
    public EventUpdateException(String message) {
        super(message);
    }
}

