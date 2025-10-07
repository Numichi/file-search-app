package com.github.numichi.exceptions;

import lombok.Getter;

/**
 * Exception thrown when there is an error accessing a directory.
 */
@Getter
public class DirectoryAccessException extends RuntimeException {
    public DirectoryAccessException(String message) {
        super(message);
    }
}