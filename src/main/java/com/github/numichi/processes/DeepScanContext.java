package com.github.numichi.processes;

import jakarta.validation.constraints.NotBlank;

public record DeepScanContext(
    @NotBlank
    String folder,

    @NotBlank
    String ext,

    boolean warn
) {
}
