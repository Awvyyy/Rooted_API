package com.example.demo.user.dto.response;

public record ChangeUserDataResponse(
        String username,
        String email,
        String profilePictureUrl,
        boolean containsProfilePicture,
        String countryCode,
        Long commends,
        Long messages
) {

}
