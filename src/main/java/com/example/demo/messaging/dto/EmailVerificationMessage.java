package com.example.demo.messaging.dto;

public record EmailVerificationMessage(
        String email,
        String verificationLink
) {
}