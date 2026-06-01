package com.example.demo.dto.request;

public record ChangeCountryRequest(
        String email,
        String password,
        String newCountryCode
) implements AuthRequest {
}
