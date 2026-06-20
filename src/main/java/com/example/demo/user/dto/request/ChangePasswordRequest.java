package com.example.demo.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotBlank(message = "Old password is required")
        @Size(min = 8, max = 128, message = "Old password must be between 8 and 128 characters")
        String oldPassword,

        @NotBlank(message = "New password is required")
        @Size(min = 8, max = 128, message = "New password must be between 8 and 128 characters")
        String newPassword
) {
}
