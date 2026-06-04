package com.example.demo.user;


import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false, unique = true, length = 33)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "is_email_verified", nullable = false)
    private boolean isEmailVerified;

    @Column(name = "contains_profile_picture", nullable = false)
    private boolean containsProfilePicture;

    @Column(name = "profile_picture_url")
    private String profilePictureUrl;

    @Column(name = "country_code", nullable = false, length = 3)
    private String countryCode;

    @Column(nullable = false)
    private Long commends = 0L;

    @Column(nullable = false)
    private Long messages = 0L;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant  createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /* constructors */

    public User(String username, String passwordHash, String email, String countryCode) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.countryCode = countryCode;
        this.isEmailVerified = false;
        this.commends = 0L;
        this.messages = 0L;
        this.containsProfilePicture = false;
    }

    protected User() {
    }

    /* getters */

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public Long getCommends() {
        return commends;
    }

    public Long getMessages() {
        return messages;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public String getEmail() {
        return email;
    }

    public boolean isEmailVerified() {
        return isEmailVerified;
    }

    public boolean isContainsProfilePicture() {
        return containsProfilePicture;
    }

    public boolean getContainsProfilePicture() {
        return containsProfilePicture;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    /* setters */

    public void rename(String username) {
        this.username = username;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public void setCommends(Long commends) {
        this.commends = commends;
    }

    public void setMessages(Long messages) {
        this.messages = messages;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void changeEmail(String email) {
        this.email = email;
    }

    public void setContainsProfilePicture(boolean containsProfilePicture){
        this.containsProfilePicture = containsProfilePicture;
    }

    public void setEmailVerified(boolean emailVerified) {
        isEmailVerified = emailVerified;
    }

    public void setProfilePictureUrl(String profilePictureUrl){
        this.profilePictureUrl = profilePictureUrl;
    }

    /* overrides */

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof User user)) {
            return false;
        }

        return id != null && id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
