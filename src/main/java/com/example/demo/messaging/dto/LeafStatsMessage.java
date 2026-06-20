package com.example.demo.messaging.dto;

import com.example.demo.messaging.LeafStatsEventType;

import java.time.Instant;
import java.util.UUID;

public record LeafStatsMessage(
        UUID eventId,
        Long leafId,
        Long branchId,
        Long userId,
        LeafStatsEventType eventType,
        Instant occurredAt
) {
}
