package com.example.demo.dto.request;

public record LoginRequest(
        String email,
        String password
) implements AuthRequest{
}
