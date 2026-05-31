package com.example.demo.dto.response;

public record ChangeUserDataResponse(
        String username,
        String email,
        String CountryCode,
        boolean containsProfilePicture,
        String profilePictureUrl
) {

}
