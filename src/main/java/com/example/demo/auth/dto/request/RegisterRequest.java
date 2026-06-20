package com.example.demo.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 33, message = "Username must be between 3 and 33 characters")
        String username,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
        String password,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "Country code is required")
        @Pattern(regexp = "^[A-Z]{2,3}$", message = "Country code must contain 2 or 3 uppercase letters")
        String countryCode
) implements AuthRequest {
}
