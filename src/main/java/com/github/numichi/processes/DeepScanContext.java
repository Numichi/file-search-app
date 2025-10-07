package com.github.numichi.processes;

import jakarta.validation.constraints.NotBlank;

/**
 * Context for performing a deep scan in a directory.
 *
 * @param folder the root folder path to scan
 * @param ext    the file extension to look for
 * @param warn   whether to enable warnings during the scan
 */
public record DeepScanContext(
    @NotBlank
    String folder,

    @NotBlank
    String ext,

    boolean warn
) {
}
