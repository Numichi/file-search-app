package com.github.numichi.exceptions;

import lombok.Getter;

@Getter
public class DirectoryAccessException extends RuntimeException {
    public DirectoryAccessException(String message) {
        super(message);
    }
}