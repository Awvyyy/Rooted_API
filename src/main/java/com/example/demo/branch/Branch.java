package com.example.demo.branch;


import com.example.demo.root.Root;
import com.example.demo.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "branches")
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "root_id", nullable = false)
    private Root root;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(name = "comments_count", nullable = false)
    private Integer commentsCount;

    @Column(nullable = false)
    private Integer rating;

    private String tags;

    @Column(name = "contains_photo", nullable = false)
    private boolean containsPhoto;

    @Column(name = "photo_url", nullable = true)
    private String photoUrl;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /* constructors */

    public Branch(Root root, User user, String title, String description, Integer commentsCount, Integer rating, String tags, boolean containsPhoto, String photoUrl) {
        this.root = root;
        this.user = user;
        this.title = title;
        this.description = description;
        this.commentsCount = commentsCount;
        this.rating = rating;
        this.tags = tags;
        this.containsPhoto = containsPhoto;
        this.photoUrl = photoUrl;
    }

    protected Branch(){
    }

    /* getters */

    public Long getId() {
        return id;
    }

    public Root getRoot() {
        return root;
    }

    public User getUser() {
        return user;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Integer getCommentsCount() {
        return commentsCount;
    }

    public Integer getRating() {
        return rating;
    }

    public String getTags() {
        return tags;
    }

    public boolean isContainsPhoto() {
        return containsPhoto;
    }

    public String getPhotoOriginalUrl() {
        return photoUrl;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    /* setters */


    public void setPhotoOriginalUrl(String photoOriginalUrl) {
        this.photoUrl = photoOriginalUrl;;
    }

    public void setContainsPhoto(boolean containsPhoto) {
        this.containsPhoto = containsPhoto;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void incrementCommentsCount() {
        this.commentsCount++;
    }

    public void decrementCommentsCount() {
        if (this.commentsCount > 0) {
            this.commentsCount--;
        }
    }
}