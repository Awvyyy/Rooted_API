package com.example.demo.dto.request;

public record ChangeUsernameRequest(
        String email,
        String password,
        String newUsername
) {
}
