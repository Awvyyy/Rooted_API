package com.example.demo.messaging.dto;

public record EmailGreetingMessage(
        String email,
        String username
) {
}