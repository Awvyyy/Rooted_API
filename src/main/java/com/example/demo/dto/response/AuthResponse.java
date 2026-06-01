package com.example.demo.dto.response;

public record AuthResponse(
     Long id,
     String username,
     String email,
     boolean containsProfilePicture,
     String profilePictureUrl,
     String countryCode
){}
