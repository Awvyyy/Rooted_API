package com.example.demo.root.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateRootDescriptionRequest(
        @NotBlank(message = "Description is required")
        @Size(max = 10_000, message = "Description must be at most 10000 characters")
        String newDescription
) {
}
