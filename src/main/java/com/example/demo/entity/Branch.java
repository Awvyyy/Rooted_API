package com.example.demo.entity;


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

    @Column(name = "comments_count", nullable = false, insertable = false)
    private Integer commentsCount;

    @Column(nullable = false, insertable = false)
    private Integer rating;

    private String tags;

    @Column(nullable = false)
    private boolean containsPhoto;

    @Column(nullable = false)
    private String photoOriginalUrl;

    @Column(nullable = false)
    private String photoStoredUrl;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /* constructors */

    public Branch(Root root, User user, String title, String description, Integer commentsCount, Integer rating, String tags, boolean containsPhoto, String photoOriginalUrl, String photoStoredUrl) {
        this.root = root;
        this.user = user;
        this.title = title;
        this.description = description;
        this.commentsCount = commentsCount;
        this.rating = rating;
        this.tags = tags;
        this.containsPhoto = containsPhoto;
        this.photoOriginalUrl = photoOriginalUrl;
        this.photoStoredUrl = photoStoredUrl;
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
        return photoOriginalUrl;
    }

    public String getPhotoStoredUrl() {
        return photoStoredUrl;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    /* setters */

    public void setPhotoStoredUrl(String photoStoredUrl) {
        this.photoStoredUrl = photoStoredUrl;
    }

    public void setPhotoOriginalUrl(String photoOriginalUrl) {
        this.photoOriginalUrl = photoOriginalUrl;
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
}