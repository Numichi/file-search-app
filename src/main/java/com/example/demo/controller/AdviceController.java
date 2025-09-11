package com.example.demo.controller;

import com.example.model.ErrorMessageResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class AdviceController {

    public static ResponseEntity<ErrorMessageResponse> commonResponseEntity(HttpStatus status) {
        return ResponseEntity.status(status).body(buildBody(status));
    }

    public static ResponseEntity<ErrorMessageResponse> commonResponseEntity(HttpStatusCode status, String message) {
        return ResponseEntity.status(status).body(buildBody(status, message));
    }

    public static ResponseEntity<ErrorMessageResponse> commonResponseEntity(HttpStatusCode status, List<String> messageList) {
        return ResponseEntity.status(status).body(buildBody(status, messageList));
    }

    public static ErrorMessageResponse buildBody(HttpStatus status) {
        return buildBody(status, status.getReasonPhrase());
    }

    public static ErrorMessageResponse buildBody(HttpStatusCode status, String message) {
        return buildBody(status, List.of(message));
    }

    public static ErrorMessageResponse buildBody(HttpStatusCode status, List<String> messageList) {
        return ErrorMessageResponse.builder()
            .status(status.value())
            .errors(messageList)
            .build();
    }

    @ExceptionHandler(HttpStatusCodeException.class)
    public ResponseEntity<ErrorMessageResponse> handleHttpStatusCodeException(HttpStatusCodeException e) {
        if (e.getStatusCode().is5xxServerError()) {
            log.error(e.getMessage(), e);
        } else if (e.getStatusCode().is4xxClientError()) {
            log.warn("Client error: {} - {}", e.getStatusCode(), e.getStatusText());
        }

        return commonResponseEntity(e.getStatusCode(), e.getStatusText());
    }

    //region 4xx errors
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorMessageResponse> handleConstraintViolationException(ConstraintViolationException e) {
        List<String> errors = new ArrayList<>();

        e.getConstraintViolations().forEach(violation -> {
            String propertyPath = violation.getPropertyPath().toString();

            String[] pathParts = propertyPath.split("\\.");
            if (pathParts.length > 2) {
                StringBuilder fieldPath = new StringBuilder();
                for (int i = 2; i < pathParts.length; i++) {
                    fieldPath.append(pathParts[i]).append(".");
                }

                fieldPath.setLength(fieldPath.length() - 1);
                errors.add(fieldPath + ": " + violation.getMessage());
            } else {
                String fieldName = propertyPath.substring(propertyPath.lastIndexOf('.') + 1);
                errors.add(fieldName + ": " + violation.getMessage());
            }
        });

        log.warn("Constraint violation exception: {}", errors);
        return commonResponseEntity(HttpStatus.BAD_REQUEST, errors);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorMessageResponse> handleValidationException(BindException e) {
        List<String> errors = new ArrayList<>();

        e.getFieldErrors().forEach(error ->
            errors.add(error.getField() + ": " + error.getDefaultMessage())
        );

        log.warn("Bind exception: {}", errors);
        return commonResponseEntity(HttpStatus.BAD_REQUEST, errors);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorMessageResponse> handleAuthenticationException(AuthenticationException e) {
        log.warn("Authentication failed: {}", e.getMessage());
        return commonResponseEntity(HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorMessageResponse> handleNoResourceFoundException(NoResourceFoundException e) {
        log.warn("Resource not found: {}", e.getResourcePath());
        return commonResponseEntity(HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorMessageResponse> handleNoSuchElementException(NoSuchElementException e) {
        log.warn("No such element exception: {}", e.getMessage());
        return commonResponseEntity(HttpStatus.NOT_FOUND);
    }
    //endregion

    //region 5xx errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessageResponse> handleRuntimeException(Exception e) {
        log.error(e.getMessage(), e);

        return commonResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }
    //endregion
}
