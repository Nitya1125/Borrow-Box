package com.borrowbox.app.models;

import java.io.Serializable;

public class User implements Serializable {
    private String id;
    private String name;
    private String email;
    private String passwordHash;
    private String salt;
    private String college;
    private String avatarUrl;
    private String phone;
    private long createdAt;

    // Computed / Joined stats (computed from queries, not stored as static values)
    private double rating;
    private int reviewCount;
    private int points;
    private int borrowsCount;
    private int lentCount;
    private String joinedDate;

    public User() {
        this.rating = 0.0;
        this.reviewCount = 0;
        this.points = 0;
        this.borrowsCount = 0;
        this.lentCount = 0;
        this.joinedDate = "";
    }

    public User(String id, String name, String email, String passwordHash, String salt, String college, String avatarUrl, String phone, long createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.college = college;
        this.avatarUrl = avatarUrl;
        this.phone = phone != null ? phone : "";
        this.createdAt = createdAt;
        this.rating = 0.0;
        this.reviewCount = 0;
        this.points = 0;
        this.borrowsCount = 0;
        this.lentCount = 0;
        this.joinedDate = "";
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name != null ? name : ""; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email != null ? email : ""; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getSalt() { return salt; }
    public void setSalt(String salt) { this.salt = salt; }

    public String getCollege() { return college != null ? college : ""; }
    public void setCollege(String college) { this.college = college; }

    public String getAvatarUrl() { return avatarUrl != null ? avatarUrl : ""; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getPhone() { return phone != null ? phone : ""; }
    public void setPhone(String phone) { this.phone = phone; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }

    public int getBorrowsCount() { return borrowsCount; }
    public void setBorrowsCount(int borrowsCount) { this.borrowsCount = borrowsCount; }

    public int getLentCount() { return lentCount; }
    public void setLentCount(int lentCount) { this.lentCount = lentCount; }

    public String getJoinedDate() { return joinedDate != null ? joinedDate : ""; }
    public void setJoinedDate(String joinedDate) { this.joinedDate = joinedDate; }
}
