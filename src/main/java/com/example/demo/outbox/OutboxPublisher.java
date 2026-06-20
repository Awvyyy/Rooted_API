package com.example.demo.outbox;

import com.example.demo.messaging.dto.EmailGreetingMessage;
import com.example.demo.messaging.dto.EmailVerificationMessage;
import com.example.demo.messaging.dto.LeafStatsMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Component
public class OutboxPublisher {

    private static final Logger log =
            LoggerFactory.getLogger(OutboxPublisher.class);

    private static final int BATCH_SIZE = 20;

    private final OutboxEventClaimer outboxEventClaimer;
    private final OutboxEventRepository outboxEventRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public OutboxPublisher(
            OutboxEventClaimer outboxEventClaimer,
            OutboxEventRepository outboxEventRepository,
            RabbitTemplate rabbitTemplate,
            ObjectMapper objectMapper
    ) {
        this.outboxEventClaimer = outboxEventClaimer;
        this.outboxEventRepository = outboxEventRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelayString = "${outbox.publisher.fixed-delay-ms:5000}")
    public void publishPendingEvents() {
        List<OutboxEvent> events =
                outboxEventClaimer.claimPendingEvents(BATCH_SIZE);

        if (events.isEmpty()) {
            return;
        }

        log.debug("Claimed {} outbox events for publishing", events.size());

        for (OutboxEvent event : events) {
            publishEvent(event);
        }
    }

    private void publishEvent(OutboxEvent event) {
        try {
            Object message = deserializePayload(event);

            rabbitTemplate.convertAndSend(
                    event.getExchangeName(),
                    event.getRoutingKey(),
                    message
            );

            event.markAsSent();
            outboxEventRepository.save(event);

            log.debug(
                    "Published outbox event id={}, type={}, routingKey={}",
                    event.getId(),
                    event.getEventType(),
                    event.getRoutingKey()
            );

        } catch (Exception exception) {
            String errorMessage = cutErrorMessage(exception);

            event.registerFailure(errorMessage);
            outboxEventRepository.save(event);

            log.warn(
                    "Failed to publish outbox event id={}, type={}, routingKey={}, error={}",
                    event.getId(),
                    event.getEventType(),
                    event.getRoutingKey(),
                    errorMessage,
                    exception
            );
        }
    }

    private Object deserializePayload(OutboxEvent event)
            throws JacksonException {
        return switch (event.getEventType()) {
            case OutboxEventService.EMAIL_VERIFICATION_REQUESTED ->
                    objectMapper.readValue(
                            event.getPayload(),
                            EmailVerificationMessage.class
                    );

            case OutboxEventService.GREETING_EMAIL_REQUESTED ->
                    objectMapper.readValue(
                            event.getPayload(),
                            EmailGreetingMessage.class
                    );

            case OutboxEventService.LEAF_LIKED,
                 OutboxEventService.LEAF_UNLIKED ->
                    objectMapper.readValue(
                            event.getPayload(),
                            LeafStatsMessage.class
                    );

            default -> throw new IllegalStateException(
                    "Unsupported outbox event type: " + event.getEventType()
            );
        };
    }

    private String cutErrorMessage(Exception exception) {
        String message = exception.getMessage();

        if (message == null) {
            return exception.getClass().getSimpleName();
        }

        if (message.length() > 1000) {
            return message.substring(0, 1000);
        }

        return message;
    }
}