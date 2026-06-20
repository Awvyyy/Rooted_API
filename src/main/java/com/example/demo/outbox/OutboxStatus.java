package com.example.demo.outbox;

public enum OutboxStatus {
    PENDING,
    PROCESSING,
    SENT,
    FAILED
}
