package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class NoteDto {

    private UUID id;

    @NotEmpty
    @NotBlank
    @Size(max = 255)
    private String title;

    private boolean checked;

    private Instant modified;
}
