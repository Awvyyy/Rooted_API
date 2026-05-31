package com.example.demo.dto.request;

public record ChangeEmailRequest(
        String email,
        String newEmail,
        String password
) {
}
