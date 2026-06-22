package com.example.demo.messaging.dto;

import java.util.UUID;

public record EmailVerificationMessage(
        UUID eventId,
        String email,
        String verificationLink
) {
}