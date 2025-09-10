package com.example.demo.exceptions;

import com.example.demo.exceptions.base.HttpUnauthorizedException;

public class LoginFailedException extends HttpUnauthorizedException {
    public LoginFailedException() {
        super("Login failed: Invalid username or password.");
    }

    public LoginFailedException(Exception e) {
        this();
        initCause(e);
    }
}
