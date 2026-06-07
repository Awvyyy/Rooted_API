package com.example.demo.user.dto.request;

public record ChangeEmailRequest(
        String newEmail,
        String password
) {}
