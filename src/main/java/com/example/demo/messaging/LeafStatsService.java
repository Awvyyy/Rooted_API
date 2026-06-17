package com.example.demo.messaging;

import com.example.demo.leaf.Leaf;
import com.example.demo.leaf.LeafRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class LeafStatsService {

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

        Leaf leaf = leafRepository.findById(leafId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Leaf not found"
                ));

        long likesCount = leafLikeRepository.countByLeaf_Id(leafId);

        // тут ты решаешь как назвать поле:
        // rating / likesCount / etc
        leaf.changeRating((int) likesCount);
    }
}