package com.compass.exception;

public class InvalidTicketDataException extends RuntimeException {
    public InvalidTicketDataException(String message) {
        super(message);
    }
}
