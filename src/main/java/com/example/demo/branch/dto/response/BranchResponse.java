package com.example.demo.branch.dto.response;

public record BranchResponse(
        String title,
        String description,
        Integer commentsCount,
        Integer rating,
        String tags,
        boolean containsPhoto,
        String photoStoredUrl,
        String authorName
) {
}
