package com.example.demo.root.dto.response;

public record RootResponse(
        String title,
        String description,
        int activityRating,
        String authorUsername
) {
}