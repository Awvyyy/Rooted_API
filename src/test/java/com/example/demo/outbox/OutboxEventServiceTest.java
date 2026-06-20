package com.example.demo.outbox;

import com.example.demo.config.RabbitConfig;
import com.example.demo.messaging.dto.EmailVerificationMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OutboxEventServiceTest {

    @Mock
    OutboxEventRepository outboxEventRepository;

    @Mock
    ObjectMapper objectMapper;

    @InjectMocks
    OutboxEventService outboxEventService;

    @Test
    void saveEmailVerificationEvent_serializesMessageAndSavesOutboxEvent() throws Exception {
        String email = "aga@example.com";
        String verificationLink = "http://localhost:8080/auth/verify?token=abc";
        String expectedJson = "{\"email\":\"aga@example.com\"}";

        when(objectMapper.writeValueAsString(any(EmailVerificationMessage.class)))
                .thenReturn(expectedJson);

        outboxEventService.saveEmailVerificationEvent(email, verificationLink);

        ArgumentCaptor<OutboxEvent> eventCaptor = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(outboxEventRepository).save(eventCaptor.capture());

        OutboxEvent event = eventCaptor.getValue();

        assertThat(event.getEventType())
                .isEqualTo(OutboxEventService.EMAIL_VERIFICATION_REQUESTED);

        assertThat(event.getExchangeName())
                .isEqualTo(RabbitConfig.EXCHANGE);

        assertThat(event.getRoutingKey())
                .isEqualTo(RabbitConfig.EMAIL_VERIFICATION_ROUTING_KEY);

        assertThat(event.getPayload())
                .isEqualTo(expectedJson);

        assertThat(event.getStatus())
                .isEqualTo(OutboxStatus.PENDING);

        assertThat((int) ReflectionTestUtils.getField(event, "attempts"))
                .isZero();
    }
}