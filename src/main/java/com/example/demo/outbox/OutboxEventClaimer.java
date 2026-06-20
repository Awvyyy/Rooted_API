package com.example.demo.outbox;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OutboxEventClaimer {

    private final OutboxEventRepository outboxEventRepository;

    public OutboxEventClaimer(OutboxEventRepository outboxEventRepository) {
        this.outboxEventRepository = outboxEventRepository;
    }

    @Transactional
    public List<OutboxEvent> claimPendingEvents(int batchSize) {
        List<OutboxEvent> events =
                outboxEventRepository.findPendingForUpdate(batchSize);

        for (OutboxEvent event : events) {
            event.markAsProcessing();
        }

        return events;
    }
}