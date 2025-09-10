package com.example.demo.exceptions.base;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

public class HttpNotFoundException extends HttpClientErrorException {
    public HttpNotFoundException() {
        this("");
    }

    public HttpNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
