package com.example.demo.user.dto.response;

public record GetUserResponse(
        String username,
        boolean containsPhoto,
        String photoUrl,
        Long commends,
        Long messages,
        String countryCode
) {
}
