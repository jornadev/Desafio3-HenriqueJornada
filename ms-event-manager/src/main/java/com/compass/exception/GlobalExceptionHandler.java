package com.compass.exception;

import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(EventNotFoundException.class)
  public ResponseEntity<String> handleEventNotFoundException(EventNotFoundException ex) {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(InvalidCepException.class)
  public ResponseEntity<String> handleInvalidCepException(InvalidCepException ex) {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(EventHasTicketsException.class)
  public ResponseEntity<String> handleEventHasTicketsException(EventHasTicketsException ex) {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(EventUpdateException.class)
  public ResponseEntity<String> handleEventUpdateException(EventUpdateException ex) {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(FeignClientException.class)
  public ResponseEntity<String> handleFeignClientException(FeignClientException ex) {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
  }

  @ExceptionHandler(FeignException.NotFound.class)
  public ResponseEntity<String> handleFeignNotFoundException(FeignException.NotFound ex) {
    return new ResponseEntity<>("Evento não encontrado no serviço externo.", HttpStatus.NOT_FOUND);
  }
}
