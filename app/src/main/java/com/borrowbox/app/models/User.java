package com.borrowbox.app.models;

import java.io.Serializable;

public class User implements Serializable {
    private String id;
    private String name;
    private String email;
    private String password;
    private String college;
    private String avatarUrl;
    private String phone;
    private double rating;
    private int reviewCount;
    private int points;
    private int borrowsCount;
    private int lentCount;
    private String joinedDate;

    public User() {}

    public User(String id, String name, String email, String password, String college, String avatarUrl) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.college = college;
        this.avatarUrl = avatarUrl;
        this.phone = "";
        this.rating = 4.9;
        this.reviewCount = 14;
        this.points = 250;
        this.borrowsCount = 8;
        this.lentCount = 12;
        this.joinedDate = "Sep 2026";
    }

    public User(String id, String name, String email, String password, String college, String avatarUrl,
                String phone, double rating, int reviewCount, int points, int borrowsCount, int lentCount, String joinedDate) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.college = college;
        this.avatarUrl = avatarUrl;
        this.phone = phone;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.points = points;
        this.borrowsCount = borrowsCount;
        this.lentCount = lentCount;
        this.joinedDate = joinedDate;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getCollege() { return college; }
    public void setCollege(String college) { this.college = college; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

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

    public String getJoinedDate() { return joinedDate; }
    public void setJoinedDate(String joinedDate) { this.joinedDate = joinedDate; }
}
