package com.example.demo.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "leaves")
public class Leaf {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String commentary;

    @Column(nullable = false, insertable = false)
    private Integer rating;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /* constructors */

    public Leaf(Branch branch, User user, String commentary, Integer rating) {
        this.branch = branch;
        this.user = user;
        this.commentary = commentary;
        this.rating = rating;
    }

    protected Leaf(){
    }

    /* getters */

    public Long getId() {
        return id;
    }

    public Branch getBranch() {
        return branch;
    }

    public User getUser() {
        return user;
    }

    public String getCommentary() {
        return commentary;
    }

    public Integer getRating() {
        return rating;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    /* setters */

   public void changeCommentary(String commentary){
       this.commentary = commentary;
   }

   public void changeRating(Integer rating){
       this.rating = rating;
   }
}
