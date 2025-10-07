package com.github.numichi.services;

import com.github.numichi.processes.DeepScanContext;
import com.github.numichi.processes.DeepScanProcess;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * Service for performing deep scans in directories to find files with specific extensions.
 */
@Validated
@Service
@RequiredArgsConstructor
public class DeepScanService {

    private final PermissionScanner permissionScanner;

    /**
     * Search for files with the given extension in the specified folder.
     *
     * @param context The context containing folder path, file extension, and warning flag.
     * @return The result of the deep scan process.
     */
    public DeepScanProcess.Result search(@Valid DeepScanContext context) {
        var process = new DeepScanProcess(permissionScanner, context);
        return process.run();
    }
}
