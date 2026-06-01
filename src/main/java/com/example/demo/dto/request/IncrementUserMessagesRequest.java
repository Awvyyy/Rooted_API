package com.example.demo.dto.request;

public record IncrementUserMessagesRequest(
        String email,
        String password,
        Integer messages
) implements AuthRequest{
}
