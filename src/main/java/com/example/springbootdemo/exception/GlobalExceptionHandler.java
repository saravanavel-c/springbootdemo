package com.example.springbootdemo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<Map<String, Object>>
    handleCustomerNotFound(CustomerNotFoundException ex) {

        return createErrorResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>>
    handleResourceNotFound(ResourceNotFoundException ex) {

        return createErrorResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
    }

    private ResponseEntity<Map<String, Object>>
    createErrorResponse(HttpStatus status, String message) {

        Map<String, Object> error = new HashMap<>();

        error.put("timestamp", LocalDateTime.now());
        error.put("status", status.value());
        error.put("message", message);

        return ResponseEntity
                .status(status)
                .body(error);
    }
}