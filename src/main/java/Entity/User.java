package Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.Instant;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String passwordHash;
    private String tokenHash;
    private String countryCode;
    private Integer commends;
    private Integer messages;
    private Instant  createdAt;
    private Instant updatedAt;

    /* constructors */

    public User(Long id, String username, String passwordHash, String tokenHash, String countryCode, Integer commends, Integer messages, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.tokenHash = tokenHash;
        this.countryCode = countryCode;
        this.commends = commends;
        this.messages = messages;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public String getTokenHash() {
        return tokenHash;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public Integer getCommends() {
        return commends;
    }

    public Integer getMessages() {
        return messages;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    /* setters */

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public void setCommends(Integer commends) {
        this.commends = commends;
    }

    public void setMessages(Integer messages) {
        this.messages = messages;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
