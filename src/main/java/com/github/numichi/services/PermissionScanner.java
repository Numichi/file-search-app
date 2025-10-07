package com.github.numichi.services;

import com.github.numichi.exceptions.DirectoryAccessException;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Service for checking read and execute permissions on directories.
 */
@Component
public class PermissionScanner {

    /**
     * Checks if the application has read and execute permissions for the specified directory.
     *
     * @param dirAbsolutePath The absolute path of the directory to check.
     * @throws IllegalArgumentException If the path is not absolute or not a directory.
     * @throws DirectoryAccessException If the directory does not exist or lacks necessary permissions.
     */
    public void checkReadPermission(Path dirAbsolutePath) {
        var absolutePath = dirAbsolutePath.toAbsolutePath().toString();

        if (!dirAbsolutePath.isAbsolute()) {
            throw new IllegalArgumentException("Path must be absolute: " + absolutePath);
        }

        if (!Files.isDirectory(dirAbsolutePath)) {
            throw new IllegalArgumentException("Path is not a directory: " + absolutePath);
        }

        if (!Files.exists(dirAbsolutePath)) {
            throw new DirectoryAccessException("Directory does not exist: " + absolutePath);
        }

        if (!Files.isExecutable(dirAbsolutePath)) {
            throw new DirectoryAccessException("Cannot enter directory (no execute permission): " + absolutePath);
        }

        if (!Files.isReadable(dirAbsolutePath)) {
            throw new DirectoryAccessException("Cannot list directory (no read permission): " + absolutePath);
        }
    }
}
