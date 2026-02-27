package com.sahil.Mark9.model;

import jakarta.persistence.*;

@Entity
public class RewardVideo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(nullable = false, length = 1000) // allow long YouTube embed URLs
    private String url;

    /*
       Optional:
       If null → common reward
       If set → specific to NUMBER or ALPHABET
    */
    @Enumerated(EnumType.STRING)
    private LearningType learningType;

    @ManyToOne
    @JoinColumn(name = "parent_id", nullable = false)
    private Parent parent;

    public RewardVideo() {}

    public RewardVideo(Long id, String title, String url, LearningType learningType, Parent parent) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.learningType = learningType;
        this.parent = parent;
    }

    // ===== Getters & Setters =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LearningType getLearningType() {
        return learningType;
    }

    public void setLearningType(LearningType learningType) {
        this.learningType = learningType;
    }

    public Parent getParent() {
        return parent;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }
}
