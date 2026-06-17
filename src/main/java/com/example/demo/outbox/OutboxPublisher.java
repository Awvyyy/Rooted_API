package com.example.demo.outbox;

import com.example.demo.messaging.dto.EmailVerificationMessage;
import com.example.demo.messaging.dto.EmailGreetingMessage;
import com.example.demo.messaging.dto.LeafLikedMessage;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OutboxPublisher {

    private static final int BATCH_SIZE = 20;

    private final OutboxEventRepository outboxEventRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public OutboxPublisher(
            OutboxEventRepository outboxEventRepository,
            RabbitTemplate rabbitTemplate,
            ObjectMapper objectMapper
    ) {
        this.outboxEventRepository = outboxEventRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelayString = "${outbox.publisher.fixed-delay-ms:5000}")
    public void publishPendingEvents() {
        List<OutboxEvent> events =
                outboxEventRepository.findByStatusOrderByCreatedAtAsc(
                        OutboxStatus.PENDING,
                        PageRequest.of(0, BATCH_SIZE)
                );

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
        } catch (Exception exception) {
            event.registerFailure(cutErrorMessage(exception));
            outboxEventRepository.save(event);
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

            case OutboxEventService.LEAF_LIKED ->
                objectMapper.readValue(
                        event.getPayload(),
                        LeafLikedMessage.class
                );

            default -> throw new IllegalStateException(
                    "Unsupported outbox event type: " + event.getEventType()
            );
        };
    }

    private String cutErrorMessage(Exception exception) {
        String message = exception.getMessage();

        if (message == null) {
            message = exception.getClass().getSimpleName();
        }

        if (message.length() > 1000) {
            return message.substring(0, 1000);
        }

        return message;
    }
}