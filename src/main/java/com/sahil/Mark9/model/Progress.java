package com.sahil.Mark9.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Progress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "child_id", nullable = false)
    private Child child;

    /*
       Generic symbol:
       "0", "7", "A", "Z"
    */
    @Column(nullable = false)
    private String symbol;

    /*
       NUMBER or ALPHABET
       Type-safe using enum
    */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LearningType learningType;

    private boolean correct;

    private Long timeTakenMs;

    private Integer tabSwitchCount;

    private LocalDateTime attemptedAt = LocalDateTime.now();

    public Progress() {}

    public Progress(Long id,
                    Child child,
                    String symbol,
                    LearningType learningType,
                    boolean correct,
                    Long timeTakenMs,
                    Integer tabSwitchCount,
                    LocalDateTime attemptedAt) {
        this.id = id;
        this.child = child;
        this.symbol = symbol;
        this.learningType = learningType;
        this.correct = correct;
        this.timeTakenMs = timeTakenMs;
        this.tabSwitchCount = tabSwitchCount;
        this.attemptedAt = attemptedAt;
    }

    // ===== Getters & Setters =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Child getChild() {
        return child;
    }

    public void setChild(Child child) {
        this.child = child;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public LearningType getLearningType() {
        return learningType;
    }

    public void setLearningType(LearningType learningType) {
        this.learningType = learningType;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public Long getTimeTakenMs() {
        return timeTakenMs;
    }

    public void setTimeTakenMs(Long timeTakenMs) {
        this.timeTakenMs = timeTakenMs;
    }

    public Integer getTabSwitchCount() {
        return tabSwitchCount;
    }

    public void setTabSwitchCount(Integer tabSwitchCount) {
        this.tabSwitchCount = tabSwitchCount;
    }

    public LocalDateTime getAttemptedAt() {
        return attemptedAt;
    }

    public void setAttemptedAt(LocalDateTime attemptedAt) {
        this.attemptedAt = attemptedAt;
    }
}
