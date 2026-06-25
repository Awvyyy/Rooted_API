package com.example.demo.messaging.idempotency;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "consumer_events",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_consumer_event",
                        columnNames = {"event_id", "consumer_name"}
                )
        }

)
public class ConsumerEvent{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @Column(name = "consumer_name", nullable = false)
    private String consumerName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ConsumerEventStatus status;

    @Column(name = "attempts")
    private int attempts;

    @Column(name = "last_error")
    private String lastError;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    protected ConsumerEvent(){}

    public ConsumerEvent(UUID eventId, String consumerName){
        this.eventId = eventId;
        this.consumerName = consumerName;
        this.status = ConsumerEventStatus.PROCESSING;
        this.attempts = 1;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isProcessing(){
        return status == ConsumerEventStatus.PROCESSING;
    }

    public boolean isProcessed(){
        return status == ConsumerEventStatus.PROCESSED;
    }

    public boolean canRetry(int maxAttempts){
        return attempts < maxAttempts;
    }

    public void markAsProcessing(){
        this.status = ConsumerEventStatus.PROCESSING;
        this.attempts++;
        this.lastError = null;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsProcessed(){
        this.status = ConsumerEventStatus.PROCESSED;
        this.lastError = null;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsFailed(String errorMessage){
        this.status = ConsumerEventStatus.FAILED;
        this.lastError = errorMessage;
        this.updatedAt = LocalDateTime.now();
    }

    /* getters */

    public Long getId() {
        return id;
    }

    public UUID getEventId() {
        return eventId;
    }

    public String getConsumerName() {
        return consumerName;
    }

    public ConsumerEventStatus getStatus() {
        return status;
    }

    public int getAttempts() {
        return attempts;
    }

    public String getLastError() {
        return lastError;
    }


}
