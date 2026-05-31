package com.example.demo.dto.request;

public record ChangePasswordRequest(
        String email,
        String password,
        String newPassword
) {
}
