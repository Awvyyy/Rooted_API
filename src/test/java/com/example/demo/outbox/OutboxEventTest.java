package com.example.demo.outbox;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class OutboxEventTest {

    @Test
    void newEvent_hasPendingStatusAndZeroAttempts() {
        OutboxEvent event = new OutboxEvent(
                "EMAIL_VERIFICATION_REQUESTED",
                "rooted.exchange",
                "email.verification",
                "{}"
        );

        assertThat(event.getStatus()).isEqualTo(OutboxStatus.PENDING);
        assertThat((int) ReflectionTestUtils.getField(event, "attempts")).isZero();
    }

    @Test
    void markAsSent_changesStatusToSentAndClearsLastError() {
        OutboxEvent event = new OutboxEvent(
                "EMAIL_VERIFICATION_REQUESTED",
                "rooted.exchange",
                "email.verification",
                "{}"
        );
        ReflectionTestUtils.setField(event, "lastError", "old error");

        event.markAsSent();

        assertThat(event.getStatus()).isEqualTo(OutboxStatus.SENT);
        assertThat(ReflectionTestUtils.getField(event, "sentAt")).isNotNull();
        assertThat(ReflectionTestUtils.getField(event, "lastError")).isNull();
    }

    @Test
    void registerFailure_beforeMaxAttempts_returnsToPending() {
        OutboxEvent event = new OutboxEvent(
                "EMAIL_VERIFICATION_REQUESTED",
                "rooted.exchange",
                "email.verification",
                "{}"
        );

        event.registerFailure("RabbitMQ is down");

        assertThat(event.getStatus()).isEqualTo(OutboxStatus.PENDING);
        assertThat((int) ReflectionTestUtils.getField(event, "attempts")).isEqualTo(1);
        assertThat(ReflectionTestUtils.getField(event, "lastError")).isEqualTo("RabbitMQ is down");
    }

    @Test
    void registerFailure_afterFiveAttempts_marksAsFailed() {
        OutboxEvent event = new OutboxEvent(
                "EMAIL_VERIFICATION_REQUESTED",
                "rooted.exchange",
                "email.verification",
                "{}"
        );

        for (int i = 0; i < 5; i++) {
            event.registerFailure("RabbitMQ is down");
        }

        assertThat(event.getStatus()).isEqualTo(OutboxStatus.FAILED);
        assertThat((int) ReflectionTestUtils.getField(event, "attempts")).isEqualTo(5);
    }
}
