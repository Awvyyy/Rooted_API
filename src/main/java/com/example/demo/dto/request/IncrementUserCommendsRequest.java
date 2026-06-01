package com.example.demo.dto.request;

public record IncrementUserCommendsRequest(
        String email,
        String password,
        Integer commends
) implements AuthRequest{
}
