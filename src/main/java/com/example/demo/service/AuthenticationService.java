package com.example.demo.service;

import com.example.demo.exceptions.base.HttpUnauthorizedException;
import com.github.f4b6a3.uuid.UuidCreator;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
public class AuthenticationService {

    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public boolean isAuthenticated() {
        var auth = getAuthentication();
        return auth != null && auth.isAuthenticated();
    }

    public boolean isAnonymous() {
        var auth = getAuthentication();
        return auth instanceof AnonymousAuthenticationToken && auth.isAuthenticated();
    }

    public String getUsername() {
        var auth = getAuthentication();

        if (auth == null) {
            throw new HttpUnauthorizedException();
        }

        return auth.getName();
    }

    public UUID getId() {
        var username = getUsername();

        if (username == null || isAnonymous()) {
            throw new HttpUnauthorizedException();
        }

        return UUID.fromString(username);
    }
}
