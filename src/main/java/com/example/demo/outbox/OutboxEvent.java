package com.example.demo.outbox;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
public class OutboxEvent {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false)
    private String exchangeName;

    @Column(nullable = false)
    private String routingKey;

    @Column(nullable = false, columnDefinition = "text")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OutboxStatus status;

    @Column(nullable = false)
    private int attempts;

    @Column(columnDefinition = "text")
    private String lastError;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime sentAt;

    private LocalDateTime processingAt;

    protected OutboxEvent() {
    }

    public OutboxEvent(
            String eventType,
            String exchangeName,
            String routingKey,
            String payload
    ) {
        this.eventType = eventType;
        this.exchangeName = exchangeName;
        this.routingKey = routingKey;
        this.payload = payload;
        this.status = OutboxStatus.PENDING;
        this.attempts = 0;
    }

    @PrePersist
    void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public String getEventType() {
        return eventType;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public String getPayload() {
        return payload;
    }

    public OutboxStatus getStatus() {
        return status;
    }

    public int getAttempts() {
        return attempts;
    }

    public LocalDateTime getProcessingAt() {
        return processingAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getLastError() {
        return lastError;
    }

    public void setProcessingAt(LocalDateTime processingAt) {
        this.processingAt = processingAt;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public void markAsProcessing() {
        this.status = OutboxStatus.PROCESSING;
        this.processingAt = LocalDateTime.now();
    }

    public void markAsSent() {
        this.status = OutboxStatus.SENT;
        this.sentAt = LocalDateTime.now();
        this.lastError = null;
    }

    public void registerFailure(String errorMessage) {
        this.attempts++;
        this.lastError = errorMessage;

        if (this.attempts >= 5) {
            this.status = OutboxStatus.FAILED;
        } else {
            this.status = OutboxStatus.PENDING;
        }
    }


}