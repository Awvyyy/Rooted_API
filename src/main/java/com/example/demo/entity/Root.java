package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "roots")
public class Root {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(name = "activity_rating", insertable = false, nullable = false)
    private Integer activityRating;

    /* constructors */

    public Root(String title, String description, Integer activityRating) {
        this.title = title;
        this.description = description;
        this.activityRating = activityRating;
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