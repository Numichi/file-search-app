package com.example.demo.exceptions.base;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

public class HttpUnauthorizedException extends HttpClientErrorException {
    public HttpUnauthorizedException() {
        this("");
    }

    public HttpUnauthorizedException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }
}
