package com.example.demo.messaging.dto;

import java.time.Instant;
import java.util.UUID;

public record LeafLikedMessage(
        UUID eventId,
        Long leafId,
        Long branchId,
        Long userId,
        Instant occurredAt

) {
}
