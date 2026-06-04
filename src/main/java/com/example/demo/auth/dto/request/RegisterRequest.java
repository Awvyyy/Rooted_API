package com.example.demo.auth.dto.request;

public record RegisterRequest (
     String username,
     String password,
     String email,
     String countryCode
) implements AuthRequest{
}
