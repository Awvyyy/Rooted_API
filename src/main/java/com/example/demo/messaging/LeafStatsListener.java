package com.example.demo.messaging;

import com.example.demo.config.RabbitConfig;
import com.example.demo.messaging.dto.LeafStatsMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class LeafStatsListener {

    private final LeafStatsService leafStatsService;

    public LeafStatsListener(LeafStatsService leafStatsService) {
        this.leafStatsService = leafStatsService;
    }

    @RabbitListener(queues = RabbitConfig.LEAF_STATS_QUEUE)
    public void handleLeafStats(LeafStatsMessage message) {
        leafStatsService.recalculateLikesCount(message.leafId());
    }
}
