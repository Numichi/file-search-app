package com.github.numichi.processes;

import com.github.numichi.exceptions.DirectoryAccessException;
import com.github.numichi.services.PermissionScanner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DeepScanProcessTest {

    @TempDir
    Path tempDir;

    private final PermissionScanner permissionScanner = new PermissionScanner();

    @Test
    void shouldFindAllFilesWithMatchingExtension() throws IOException {
        var subDir1 = Files.createDirectory(tempDir.resolve("subdir1"));
        var nestedDir = Files.createDirectory(subDir1.resolve("nested"));
        var subDir2 = Files.createDirectory(tempDir.resolve("subdir2"));

        Files.createFile(tempDir.resolve("file1.txt"));
        Files.createFile(tempDir.resolve("file2.log"));
        Files.createFile(subDir1.resolve("file1.txt"));
        Files.createFile(subDir2.resolve("file2.txt"));
        Files.createFile(nestedDir.resolve("file3.txt"));
        Files.createFile(nestedDir.resolve("file5.csv"));

        var context = new DeepScanContext(tempDir.toAbsolutePath().toString(), ".txt", false);
        var process = new DeepScanProcess(permissionScanner, context);
        var result = process.run();

        assertThat(result.fileNames()).hasSize(3);
        assertThat(result.fileNames()).anyMatch(path -> path.equals("file1.txt"))
            .anyMatch(path -> path.equals("file2.txt"))
            .anyMatch(path -> path.equals("file3.txt"))
            .noneMatch(path -> path.equals("file2.log"))
            .noneMatch(path -> path.equals("file5.csv"));
        assertThat(result.permissionProblems()).isEmpty();
    }

    @Test
    void shouldHandleEmptyDirectory() {
        DeepScanContext context = new DeepScanContext(tempDir.toAbsolutePath().toString(), ".txt", false);
        DeepScanProcess process = new DeepScanProcess(permissionScanner, context);
        DeepScanProcess.Result result = process.run();

        assertThat(result.fileNames()).isEmpty();
        assertThat(result.permissionProblems()).isEmpty();
    }

    @Test
    void shouldHandlePermissionErrorOnStartPath() {
        PermissionScanner mockScanner = mock(PermissionScanner.class);

        doThrow(new DirectoryAccessException("Access denied to subdirectory")).when(mockScanner).checkReadPermission(any());

        DeepScanContext context = new DeepScanContext(tempDir.toAbsolutePath().toString(), ".txt", false);
        DeepScanProcess process = new DeepScanProcess(mockScanner, context);

        assertThatThrownBy(process::run)
            .isInstanceOf(DirectoryAccessException.class)
            .hasMessageContaining("Access denied to subdirectory");
    }

    @Test
    void shouldThrowPermissionErrorWithWarnFalse() throws IOException {
        Path subDir = Files.createDirectory(tempDir.resolve("restricted"));

        PermissionScanner mockScanner = mock(PermissionScanner.class);

        doAnswer(invocation -> {
            Path path = invocation.getArgument(0);
            if (path.equals(subDir)) {
                throw new DirectoryAccessException("Access denied");
            }
            return null;
        }).when(mockScanner).checkReadPermission(any(Path.class));

        DeepScanContext context = new DeepScanContext(tempDir.toAbsolutePath().toString(), ".txt", false);
        DeepScanProcess process = new DeepScanProcess(mockScanner, context);

        assertThatThrownBy(process::run)
            .isInstanceOf(DirectoryAccessException.class)
            .hasMessageContaining("Access denied");
    }

    @Test
    void shouldThrowPermissionErrorWithWarnTrue() throws IOException {
        Path restrictedDir = Files.createDirectory(tempDir.resolve("restricted"));
        Path subDir = Files.createDirectory(tempDir.resolve("subdir"));

        Files.createFile(tempDir.resolve("root.txt"));
        Files.createFile(subDir.resolve("sub.txt"));

        PermissionScanner mockScanner = mock(PermissionScanner.class);

        doAnswer(invocation -> {
            Path path = invocation.getArgument(0);
            if (path.equals(restrictedDir)) {
                throw new DirectoryAccessException("Access denied");
            }
            return null;
        }).when(mockScanner).checkReadPermission(any(Path.class));

        var context = new DeepScanContext(tempDir.toAbsolutePath().toString(), ".txt", true);
        var process = new DeepScanProcess(mockScanner, context);
        var result = process.run();

        assertThat(result.fileNames()).hasSize(2);
        assertThat(result.fileNames()).anyMatch(path -> path.equals("root.txt"))
            .anyMatch(path -> path.equals("sub.txt"));

        assertThat(result.permissionProblems()).hasSize(1);
        assertThat(result.permissionProblems().getFirst()).contains("Access denied");
    }
}

