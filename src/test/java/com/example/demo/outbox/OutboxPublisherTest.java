package com.example.demo.outbox;

import com.example.demo.messaging.dto.EmailVerificationMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxPublisherTest {

    @Mock
    OutboxEventClaimer outboxEventClaimer;

    @Mock
    OutboxEventRepository outboxEventRepository;

    @Mock
    RabbitTemplate rabbitTemplate;

    @Mock
    ObjectMapper objectMapper;

    @Test
    void publishPendingEvents_whenRabbitSendSucceeds_marksEventAsSent() throws Exception {
        OutboxEvent event = new OutboxEvent(
                OutboxEventService.EMAIL_VERIFICATION_REQUESTED,
                "rooted.exchange",
                "email.verification",
                "{\"email\":\"aga@example.com\",\"verificationLink\":\"http://localhost:8080/auth/verify?token=abc\"}"
        );

        EmailVerificationMessage message = new EmailVerificationMessage(
                UUID.randomUUID(),
                "aga@example.com",
                "http://localhost:8080/auth/verify?token=abc"
        );

        when(outboxEventClaimer.claimPendingEvents(20))
                .thenReturn(List.of(event));

        when(objectMapper.readValue(
                event.getPayload(),
                EmailVerificationMessage.class
        )).thenReturn(message);

        OutboxPublisher publisher = new OutboxPublisher(
                outboxEventClaimer,
                outboxEventRepository,
                rabbitTemplate,
                objectMapper
        );

        publisher.publishPendingEvents();

        verify(rabbitTemplate).convertAndSend(
                event.getExchangeName(),
                event.getRoutingKey(),
                message
        );

        verify(outboxEventRepository).save(event);

        assertThat(event.getStatus()).isEqualTo(OutboxStatus.SENT);
        assertThat(event.getLastError()).isNull();
        assertThat(event.getSentAt()).isNotNull();
    }

    @Test
    void publishPendingEvents_whenRabbitSendFails_registersFailure() throws Exception {
        OutboxEvent event = new OutboxEvent(
                OutboxEventService.EMAIL_VERIFICATION_REQUESTED,
                "rooted.exchange",
                "email.verification",
                "{\"email\":\"aga@example.com\",\"verificationLink\":\"http://localhost:8080/auth/verify?token=abc\"}"
        );

        EmailVerificationMessage message = new EmailVerificationMessage(
                UUID.randomUUID(),
                "aga@example.com",
                "http://localhost:8080/auth/verify?token=abc"
        );

        when(outboxEventClaimer.claimPendingEvents(20))
                .thenReturn(List.of(event));

        when(objectMapper.readValue(
                event.getPayload(),
                EmailVerificationMessage.class
        )).thenReturn(message);

        doThrow(new RuntimeException("RabbitMQ is down"))
                .when(rabbitTemplate)
                .convertAndSend(
                        event.getExchangeName(),
                        event.getRoutingKey(),
                        message
                );

        OutboxPublisher publisher = new OutboxPublisher(
                outboxEventClaimer,
                outboxEventRepository,
                rabbitTemplate,
                objectMapper
        );

        publisher.publishPendingEvents();

        verify(outboxEventRepository).save(event);

        assertThat(event.getStatus()).isEqualTo(OutboxStatus.PENDING);
        assertThat(event.getAttempts()).isEqualTo(1);
        assertThat(event.getLastError()).contains("RabbitMQ is down");
    }
}