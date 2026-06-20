package com.example.demo.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ChangeCountryRequest(
        @NotBlank(message = "Country code is required")
        @Pattern(regexp = "^[A-Z]{2,3}$", message = "Country code must contain 2 or 3 uppercase letters")
        String newCountryCode
) {
}
