package com.example.demo.dto.request;

public record ChangeProfilePictureRequest(
        String email,
        String password,
        String profilePictureUrl
) {
}
