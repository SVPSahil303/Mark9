package com.sahil.Mark9.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Parent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    private String phone;

    private String pinHash;
    
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Child> children = new ArrayList<>();

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RewardVideo> rewardVideos = new ArrayList<>();

    public Parent() {}

    public Parent(Long id, String fullName, String email, String password, String phone) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phone = phone;
    }

    

    // ===== Helper Methods (Professional Touch) =====

    public Parent(Long id, String fullName, String email, String password, String phone, String pinHash,
            List<Child> children, List<RewardVideo> rewardVideos) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.pinHash = pinHash;
        this.children = children;
        this.rewardVideos = rewardVideos;
    }

    public void addChild(Child child) {
        children.add(child);
        child.setParent(this);
    }

    public void removeChild(Child child) {
        children.remove(child);
        child.setParent(null);
    }

    public void addRewardVideo(RewardVideo video) {
        rewardVideos.add(video);
        video.setParent(this);
    }

    public void removeRewardVideo(RewardVideo video) {
        rewardVideos.remove(video);
        video.setParent(null);
    }

    // ===== Getters & Setters =====

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public List<Child> getChildren() {
        return children;
    }

    public List<RewardVideo> getRewardVideos() {
        return rewardVideos;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setChildren(List<Child> children) {
        this.children = children;
    }

    public void setRewardVideos(List<RewardVideo> rewardVideos) {
        this.rewardVideos = rewardVideos;
    }

    public String getPinHash() {
        return pinHash;
    }

    public void setPinHash(String pinHash) {
        this.pinHash = pinHash;
    }

    
}
