package com.example.demo.messaging;

import com.example.demo.config.RabbitConfig;
import com.example.demo.emailVerification.EmailService;
import com.example.demo.messaging.dto.EmailGreetingMessage;
import com.example.demo.messaging.dto.EmailVerificationMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class EmailMessageListener {

    private final EmailService emailService;

    public EmailMessageListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues = RabbitConfig.EMAIL_VERIFICATION_QUEUE)
    public void receiveEmailVerificationMessage(EmailVerificationMessage message) {
        emailService.sendVerificationEmail(
                message.email(),
                message.verificationLink()
        );
    }

    @RabbitListener(queues = RabbitConfig.EMAIL_GREETING_QUEUE)
    public void receiveEmailGreetingMessage(EmailGreetingMessage message) {
        emailService.sendGreetingEmail(
                message.email(),
                message.username()
        );
    }
}
