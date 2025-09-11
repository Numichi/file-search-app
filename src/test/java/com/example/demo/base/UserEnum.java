package com.example.demo.base;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public enum UserEnum {
    USER_1("testuser1", "password", UUID.fromString("01992abc-3c1c-7305-bf26-c5fdb14ff811")),
    USER_2("testuser2", "password", UUID.fromString("01992abc-3c1c-7305-bf26-c5fdb14ff812")),
    ADMIN("testadmin", "password", UUID.fromString("01992abc-3c1c-7305-bf26-c5fdb14ff813"));

    private final String username;
    private final String password;
    private final UUID id;
}
