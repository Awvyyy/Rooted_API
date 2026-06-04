package com.example.demo.user.dto.request;

import com.example.demo.auth.dto.request.AuthRequest;

public record ChangeProfilePictureRequest(
        String profilePictureUrl
)  {
}
