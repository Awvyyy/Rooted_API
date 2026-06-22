package com.example.demo.messaging.idempotency;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class IdempotencyService {

    private static final int MAX_ATTEMPTS = 5;

    private final ConsumerEventRepository consumerEventRepository;

    public IdempotencyService(
            ConsumerEventRepository consumerEventRepository
    ) {
        this.consumerEventRepository = consumerEventRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean tryStartProcessing(
            UUID eventId,
            String consumerName
    ) {
        return consumerEventRepository.findForUpdate(eventId, consumerName)
                .map(event -> handleExistingEvent(event, consumerName))
                .orElseGet(() -> createProcessingEvent(eventId, consumerName));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markProcessed(
            UUID eventId,
            String consumerName
    ) {
        ConsumerEvent event = consumerEventRepository
                .findForUpdate(eventId, consumerName)
                .orElseThrow(() -> new IllegalStateException(
                        "Consumer event not found: " + eventId
                ));

        event.markAsProcessed();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markFailed(
            UUID eventId,
            String consumerName,
            Exception exception
    ) {
        ConsumerEvent event = consumerEventRepository
                .findForUpdate(eventId, consumerName)
                .orElseThrow(() -> new IllegalStateException(
                        "Consumer event not found: " + eventId
                ));

        event.markAsFailed(cutErrorMessage(exception));
    }

    private boolean handleExistingEvent(
            ConsumerEvent event,
            String consumerName
    ) {
        if (event.isProcessed()) {
            return false;
        }

        if (!event.canRetry(MAX_ATTEMPTS)) {
            return false;
        }

        event.markAsProcessing();

        return true;
    }

    private boolean createProcessingEvent(
            UUID eventId,
            String consumerName
    ) {
        try {
            consumerEventRepository.saveAndFlush(
                    new ConsumerEvent(eventId, consumerName)
            );

            return true;
        } catch (DataIntegrityViolationException exception) {
            return false;
        }
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