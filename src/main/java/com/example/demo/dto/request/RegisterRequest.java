package com.example.demo.dto.request;

public record RegisterRequest (
     String username,
     String password,
     String email,
     String countryCode
) implements AuthRequest{
}
