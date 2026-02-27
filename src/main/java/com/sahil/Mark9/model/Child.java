package com.sahil.Mark9.model;

import jakarta.persistence.*;

@Entity
public class Child {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String childName;

    private Integer age;

    // 🔐 login credentials for child
    @Column(unique = true, nullable = false)
    private String username;

    private String password;

    /* ==============================
       🎯 DIGIT MODULE TRACKING
       ============================== */

    private Integer totalDigitsLearned = 0;

    private Integer lastDigitPracticed;   // 0–9

    // Goal for digits
    private Integer digitGoal = 10;   // default 10


    /* ==============================
       🎯 ALPHABET MODULE TRACKING
       ============================== */

    private Integer totalAlphabetsLearned = 0;

    private String lastAlphabetPracticed;  // A–Z

    // Goal for alphabets
    private Integer alphabetGoal = 26;   // default full alphabet


    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Parent parent;

    public Child() {}

    public Child(Long id, String childName, Integer age, String username, String password,
                 Integer totalDigitsLearned, Integer lastDigitPracticed, Integer digitGoal,
                 Integer totalAlphabetsLearned, String lastAlphabetPracticed, Integer alphabetGoal,
                 Parent parent) {
        this.id = id;
        this.childName = childName;
        this.age = age;
        this.username = username;
        this.password = password;
        this.totalDigitsLearned = totalDigitsLearned;
        this.lastDigitPracticed = lastDigitPracticed;
        this.digitGoal = digitGoal;
        this.totalAlphabetsLearned = totalAlphabetsLearned;
        this.lastAlphabetPracticed = lastAlphabetPracticed;
        this.alphabetGoal = alphabetGoal;
        this.parent = parent;
    }

    /* ==============================
       GETTERS & SETTERS
       ============================== */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /* ===== DIGIT GETTERS & SETTERS ===== */

    public Integer getTotalDigitsLearned() {
        return totalDigitsLearned;
    }

    public void setTotalDigitsLearned(Integer totalDigitsLearned) {
        this.totalDigitsLearned = totalDigitsLearned;
    }

    public Integer getLastDigitPracticed() {
        return lastDigitPracticed;
    }

    public void setLastDigitPracticed(Integer lastDigitPracticed) {
        this.lastDigitPracticed = lastDigitPracticed;
    }

    public Integer getDigitGoal() {
        return digitGoal;
    }

    public void setDigitGoal(Integer digitGoal) {
        this.digitGoal = digitGoal;
    }

    /* ===== ALPHABET GETTERS & SETTERS ===== */

    public Integer getTotalAlphabetsLearned() {
        return totalAlphabetsLearned;
    }

    public void setTotalAlphabetsLearned(Integer totalAlphabetsLearned) {
        this.totalAlphabetsLearned = totalAlphabetsLearned;
    }

    public String getLastAlphabetPracticed() {
        return lastAlphabetPracticed;
    }

    public void setLastAlphabetPracticed(String lastAlphabetPracticed) {
        this.lastAlphabetPracticed = lastAlphabetPracticed;
    }

    public Integer getAlphabetGoal() {
        return alphabetGoal;
    }

    public void setAlphabetGoal(Integer alphabetGoal) {
        this.alphabetGoal = alphabetGoal;
    }

    /* ===== RELATION ===== */

    public Parent getParent() {
        return parent;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }
}
