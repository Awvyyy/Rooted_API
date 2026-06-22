package com.example.demo.messaging;

import com.example.demo.config.RabbitConfig;
import com.example.demo.emailVerification.EmailService;
import com.example.demo.messaging.dto.EmailGreetingMessage;
import com.example.demo.messaging.dto.EmailVerificationMessage;
import com.example.demo.messaging.idempotency.IdempotencyService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class EmailMessageListener {

    private static final String EMAIL_VERIFICATION_CONSUMER =
            "email-verification-consumer";

    private static final String EMAIL_GREETING_CONSUMER =
            "email-greeting-consumer";

    private final EmailService emailService;
    private final IdempotencyService idempotencyService;

    public EmailMessageListener(
            EmailService emailService,
            IdempotencyService idempotencyService
    ) {
        this.emailService = emailService;
        this.idempotencyService = idempotencyService;
    }

    @RabbitListener(queues = RabbitConfig.EMAIL_VERIFICATION_QUEUE)
    public void receiveEmailVerificationMessage(
            EmailVerificationMessage message
    ) {
        boolean shouldProcess = idempotencyService.tryStartProcessing(
                message.eventId(),
                EMAIL_VERIFICATION_CONSUMER
        );

        if (!shouldProcess) {
            return;
        }

        try {
            emailService.sendVerificationEmail(
                    message.email(),
                    message.verificationLink()
            );

            idempotencyService.markProcessed(
                    message.eventId(),
                    EMAIL_VERIFICATION_CONSUMER
            );
        } catch (Exception exception) {
            idempotencyService.markFailed(
                    message.eventId(),
                    EMAIL_VERIFICATION_CONSUMER,
                    exception
            );

            throw exception;
        }
    }

    @RabbitListener(queues = RabbitConfig.EMAIL_GREETING_QUEUE)
    public void receiveEmailGreetingMessage(
            EmailGreetingMessage message
    ) {
        boolean shouldProcess = idempotencyService.tryStartProcessing(
                message.eventId(),
                EMAIL_GREETING_CONSUMER
        );

        if (!shouldProcess) {
            return;
        }

        try {
            emailService.sendGreetingEmail(
                    message.email(),
                    message.username()
            );

            idempotencyService.markProcessed(
                    message.eventId(),
                    EMAIL_GREETING_CONSUMER
            );
        } catch (Exception exception) {
            idempotencyService.markFailed(
                    message.eventId(),
                    EMAIL_GREETING_CONSUMER,
                    exception
            );

            throw exception;
        }
    }
}