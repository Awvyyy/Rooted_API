package com.example.demo.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangeUsernameRequest(
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 33, message = "Username must be between 3 and 33 characters")
        String newUsername
) {
}
