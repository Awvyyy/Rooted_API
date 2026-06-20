package com.example.demo.messaging;

import com.example.demo.leaf.Leaf;
import com.example.demo.leaf.LeafRepository;
import com.example.demo.outbox.OutboxPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LeafStatsService {

    private static final Logger log =
            LoggerFactory.getLogger(LeafStatsService.class);

    private final LeafRepository leafRepository;
    private final LeafLikeRepository leafLikeRepository;

    public LeafStatsService(
            LeafRepository leafRepository,
            LeafLikeRepository leafLikeRepository
    ) {
        this.leafRepository = leafRepository;
        this.leafLikeRepository = leafLikeRepository;
    }

    @Transactional
    public void recalculateLikesCount(Long leafId) {
        leafRepository.findById(leafId).ifPresentOrElse(
                leaf -> updateLikesCount(leaf, leafId),
                () -> log.debug(
                        "Skip recalculating likes count. Leaf not found, leafId={}",
                        leafId
                )
        );
    }

    private void updateLikesCount(Leaf leaf, Long leafId) {
        long likesCount = leafLikeRepository.countByLeaf_Id(leafId);

        if (likesCount > Integer.MAX_VALUE) {
            throw new IllegalStateException(
                    "Too many likes for leafId=" + leafId
            );
        }

        leaf.changeRating((int) likesCount);
    }
}