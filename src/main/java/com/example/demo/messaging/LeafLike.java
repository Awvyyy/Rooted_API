package com.example.demo.messaging;

import com.example.demo.leaf.Leaf;
import com.example.demo.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "leaf_likes",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_leaf_likes_leaf_user",
                        columnNames = {"leaf_id", "user_id"}
                )
        },
        indexes = {
                @Index(name = "idx_leaf_likes_leaf_id", columnList = "leaf_id"),
                @Index(name = "idx_leaf_likes_user_id", columnList = "user_id")
        }
)
public class LeafLike {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "leaf_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_leaf_likes_leaf")
    )
    private Leaf leaf;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_leaf_likes_user")
    )
    private User user;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected LeafLike() {
    }

    public LeafLike(Leaf leaf, User user) {
        this.leaf = leaf;
        this.user = user;
    }

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public Leaf getLeaf() {
        return leaf;
    }

    public User getUser() {
        return user;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}