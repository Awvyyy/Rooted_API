package com.example.demo.auth.dto.request;

public record LoginRequest(
        String email,
        String password
) implements AuthRequest{
}
