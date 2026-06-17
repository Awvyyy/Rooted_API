package com.example.demo.emailVerification;

import com.example.demo.outbox.OutboxEventService;
import com.example.demo.user.User;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class VerificationService {

    private final EmailTokenRepository tokenRepository;
    private final OutboxEventService outboxEventService;

    public VerificationService(
            EmailTokenRepository tokenRepository,
            OutboxEventService outboxEventService
    ) {
        this.tokenRepository = tokenRepository;
        this.outboxEventService = outboxEventService;
    }

    public void sendVerificationEmail(User user) {

        tokenRepository.deleteByUser(user);

        String token = UUID.randomUUID().toString();

        EmailToken emailToken =
                new EmailToken(
                        token,
                        user,
                        LocalDateTime.now().plusHours(24)
                );

        tokenRepository.save(emailToken);

        String verificationLink =
                "http://localhost:8080/auth/verify?token=" + token;

        String email = user.getEmail();

        outboxEventService.saveEmailVerificationEvent(email, verificationLink);
    }

    @Transactional
    public void verifyEmail(String token) {

        EmailToken emailToken =
                tokenRepository.findByToken(token)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Invalid token"
                                ));

        if (emailToken.getExpiresAt()
                .isBefore(LocalDateTime.now())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Token expired"
            );
        }

        User user = emailToken.getUser();

        if (user.isEmailVerified()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Email already verified"
            );
        }

        user.setEmailVerified(true);

        tokenRepository.delete(emailToken);

        String email = user.getEmail();
        String username = user.getUsername();

        outboxEventService.saveGreetingEmailEvent(email, username);
    }
}
