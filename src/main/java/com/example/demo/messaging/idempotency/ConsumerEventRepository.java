package com.example.demo.messaging.idempotency;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface ConsumerEventRepository extends JpaRepository<ConsumerEvent, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select event
            from ConsumerEvent event
            where event.eventId = :eventId
             and event.consumerName = :consumerName
""")
    Optional<ConsumerEvent> findForUpdate(
            UUID eventId,
            String consumerName
    );
}
