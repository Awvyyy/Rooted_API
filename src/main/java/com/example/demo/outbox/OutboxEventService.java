package com.example.demo.outbox;

import com.example.demo.config.RabbitConfig;
import com.example.demo.messaging.LeafStatsEventType;
import com.example.demo.messaging.dto.EmailGreetingMessage;
import com.example.demo.messaging.dto.EmailVerificationMessage;
import com.example.demo.messaging.dto.LeafStatsMessage;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.UUID;

@Service
public class OutboxEventService {

    public static final String EMAIL_VERIFICATION_REQUESTED =
            "EMAIL_VERIFICATION_REQUESTED";

    public static final String GREETING_EMAIL_REQUESTED =
            "GREETING_EMAIL_REQUESTED";

    public static final String LEAF_LIKED =
            "LEAF_LIKED";

    public static final String LEAF_UNLIKED =
            "LEAF_DISLIKED";

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public OutboxEventService(
            OutboxEventRepository outboxEventRepository,
            ObjectMapper objectMapper
    ) {
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
    }

    public void saveEmailVerificationEvent(
            String email,
            String verificationLink
    ) {
        UUID eventId = UUID.randomUUID();

        EmailVerificationMessage message = new EmailVerificationMessage(
                eventId,
                email,
                verificationLink
        );

        saveEvent(
                EMAIL_VERIFICATION_REQUESTED,
                RabbitConfig.EXCHANGE,
                RabbitConfig.EMAIL_VERIFICATION_ROUTING_KEY,
                message
        );
    }

    public void saveGreetingEmailEvent(
            String email,
            String username
    ) {
        UUID eventId = UUID.randomUUID();

        EmailGreetingMessage message = new EmailGreetingMessage(
                eventId,
                email,
                username
        );

        saveEvent(
                GREETING_EMAIL_REQUESTED,
                RabbitConfig.EXCHANGE,
                RabbitConfig.EMAIL_GREETING_ROUTING_KEY,
                message
        );
    }

    public void saveLeafLikedEvent(
            Long leafId,
            Long branchId,
            Long userId
    ){
        LeafStatsMessage message = new LeafStatsMessage(
                UUID.randomUUID(),
                leafId,
                branchId,
                userId,
                LeafStatsEventType.LIKED,
                Instant.now());

        saveEvent(
                LEAF_LIKED,
                RabbitConfig.EXCHANGE,
                RabbitConfig.LEAF_LIKED_ROUTING_KEY,
                message
        );
    }

    public void saveLeafUnlikedEvent(
            Long leafId,
            Long branchId,
            Long userId
    ){
        LeafStatsMessage message = new LeafStatsMessage(
                UUID.randomUUID(),
                leafId,
                branchId,
                userId,
                LeafStatsEventType.UNLIKED,
                Instant.now());

        saveEvent(
                LEAF_UNLIKED,
                RabbitConfig.EXCHANGE,
                RabbitConfig.LEAF_UNLIKED_ROUTING_KEY,
                message
        );
    }

    private void saveEvent(
            String eventType,
            String exchangeName,
            String routingKey,
            Object payload
    ) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(payload);

            OutboxEvent event = new OutboxEvent(
                    eventType,
                    exchangeName,
                    routingKey,
                    jsonPayload
            );

            outboxEventRepository.save(event);
        } catch (JacksonException exception) {
            throw new IllegalStateException(
                    "Failed to serialize outbox event payload",
                    exception
            );
        }
    }
}