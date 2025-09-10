package com.example.demo.base;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserEnum {
    USER_1("testuser1", "password"),
    USER_2("testuser2", "password"),
    ADMIN("testadmin", "password");

    private final String username;
    private final String password;
}
