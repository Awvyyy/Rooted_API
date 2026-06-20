package com.example.demo.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangeProfilePictureRequest(
        @NotBlank(message = "Profile picture URL is required")
        @Size(max = 2_000, message = "Profile picture URL must be at most 2000 characters")
        String profilePictureUrl
) {
}
