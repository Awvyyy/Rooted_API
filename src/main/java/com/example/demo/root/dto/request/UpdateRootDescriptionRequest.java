package com.example.demo.root.dto.request;

public record UpdateRootDescriptionRequest(
        String title,
        String newDescription
) implements RootOwnershipRequest {
}