package com.example.demo.dto;

import com.example.demo.config.validator.Password;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserDto {
    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9_\\-.]{1,255}$", message = "Username can only contain letters, numbers, underscores, hyphens, and periods, and must be between 1 and 255 characters long.")
    private String username;

    @NotNull
    @Password
    private String password;

    @NotEmpty
    @NotBlank
    @Email
    @Size(max = 255)
    private String email;
}
