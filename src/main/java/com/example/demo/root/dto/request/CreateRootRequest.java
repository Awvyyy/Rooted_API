package com.example.demo.root.dto.request;

import com.example.demo.auth.dto.request.AuthRequest;

public record CreateRootRequest(
        String title,
        String description
) {
}
