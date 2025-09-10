package com.example.demo.controller;

import com.example.model.ErrorMessageResponse;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class AdviceController {

    public static ResponseEntity<ErrorMessageResponse> commonResponse(HttpStatus status) {
        return commonResponse(status, status.getReasonPhrase());
    }

    public static ResponseEntity<ErrorMessageResponse> commonResponse(HttpStatusCode status, String message) {
        return commonResponse(status, message == null || message.isEmpty() ? List.of() : List.of(message));
    }

    public static ResponseEntity<ErrorMessageResponse> commonResponse(
        HttpStatusCode status,
        List<String> messageList
    ) {
        var body = ErrorMessageResponse.builder()
            .status(status.value())
            .errors(messageList)
            .build();

        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessageResponse> handleRuntimeException(Exception e) {
        log.error(e.getMessage(), e);

        return commonResponse(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorMessageResponse> handleAuthenticationException(AuthenticationException e) {
        return commonResponse(HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorMessageResponse> handleValidationException(ValidationException e) {
        return commonResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorMessageResponse> handleNoSuchElementException(NoSuchElementException e) {
        return commonResponse(HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HttpStatusCodeException.class)
    public ResponseEntity<ErrorMessageResponse> handleHttpStatusCodeException(HttpStatusCodeException e) {
        if (e.getStatusCode().is5xxServerError()) {
            log.error(e.getMessage(), e);
        }

        return commonResponse(e.getStatusCode(), e.getStatusText());
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorMessageResponse> handleValidationException(BindException e) {
        List<String> errors = new ArrayList<>(4);

        e.getBindingResult()
            .getFieldErrors()
            .forEach(error ->
                errors.add(error.getField() + ": " + error.getDefaultMessage())
            );

        return commonResponse(HttpStatus.BAD_REQUEST, errors);
    }
}
