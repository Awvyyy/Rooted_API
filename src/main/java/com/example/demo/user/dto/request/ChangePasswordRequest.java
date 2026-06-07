package com.example.demo.user.dto.request;

public record ChangePasswordRequest(
        String oldPassword,
        String newPassword
)  {
}
