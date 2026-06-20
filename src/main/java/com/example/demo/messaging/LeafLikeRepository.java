package com.example.demo.messaging;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LeafLikeRepository extends JpaRepository<LeafLike, UUID> {
    boolean existsByLeaf_IdAndUser_Id(Long leafId, Long userId);

    Optional<LeafLike> findByLeaf_IdAndUser_Id(Long leafId, Long userId);

    long countByLeaf_Id(Long leafId);

    void deleteByLeaf_Id(Long leafId);
}
