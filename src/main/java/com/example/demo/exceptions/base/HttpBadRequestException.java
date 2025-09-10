package com.example.demo.exceptions.base;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

public class HttpBadRequestException extends HttpClientErrorException {
    public HttpBadRequestException() {
        this("");
    }

    public HttpBadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
