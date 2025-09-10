package com.example.demo.exceptions.base;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

public class HttpForbiddenException extends HttpClientErrorException {
    public HttpForbiddenException() {
        this("");
    }

    public HttpForbiddenException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}
