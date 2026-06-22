package com.example.demo.messaging.dto;

import java.util.UUID;

public record EmailGreetingMessage(
        UUID eventId,
        String email,
        String username
) {
}