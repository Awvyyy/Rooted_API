package com.example.demo.root;

import com.example.demo.user.User;
import jakarta.persistence.*;

@Entity
@Table(name = "roots")
public class Root {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User User;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(name = "activity_rating", nullable = false)
    private Integer activityRating;

    /* constructors */

    public Root(String title, String description, Integer activityRating, User user) {
        this.title = title;
        this.description = description;
        this.activityRating = 0;
        this.User = user;
    }

    protected Root() {

    }

    /* getters */

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Integer getActivityRating() {
        return activityRating;
    }

    public User getUser() {
        return User;
    }

    /* setters */

    public void renameTitle(String title){
        this.title = title;
    }

    public void changeDescription(String description){
        this.description = description;
    }

    public void setActivityRating(Integer activityRating){
        this.activityRating = activityRating;
    }
}