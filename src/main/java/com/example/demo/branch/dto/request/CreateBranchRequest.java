package com.example.demo.branch.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateBranchRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 255, message = "Title must be at most 255 characters")
        String title,

        @NotBlank(message = "Description is required")
        @Size(max = 10_000, message = "Description must be at most 10000 characters")
        String description,

        @NotBlank(message = "Root title is required")
        @Size(max = 255, message = "Root title must be at most 255 characters")
        String rootTitle,

        @Size(max = 255, message = "Tags must be at most 255 characters")
        String tags,

        @Size(max = 2_000, message = "Photo URL must be at most 2000 characters")
        String photoOriginalUrl
) {
}
