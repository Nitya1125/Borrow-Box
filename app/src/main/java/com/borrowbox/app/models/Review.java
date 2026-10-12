package com.borrowbox.app.models;

import java.io.Serializable;

public class Review implements Serializable {
    private String id;
    private String transactionId;
    private String reviewerId;
    private String revieweeId;
    private float rating;
    private String comment;
    private long createdAt;

    // Joined fields
    private String reviewerName;
    private String reviewerAvatar;

    public Review() {}

    public Review(String id, String transactionId, String reviewerId, String revieweeId, float rating, String comment, long createdAt) {
        this.id = id;
        this.transactionId = transactionId;
        this.reviewerId = reviewerId;
        this.revieweeId = revieweeId;
        this.rating = rating;
        this.comment = comment != null ? comment : "";
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getReviewerId() { return reviewerId; }
    public void setReviewerId(String reviewerId) { this.reviewerId = reviewerId; }

    public String getRevieweeId() { return revieweeId; }
    public void setRevieweeId(String revieweeId) { this.revieweeId = revieweeId; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public String getComment() { return comment != null ? comment : ""; }
    public void setComment(String comment) { this.comment = comment; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public String getReviewerName() { return reviewerName != null ? reviewerName : ""; }
    public void setReviewerName(String reviewerName) { this.reviewerName = reviewerName; }

    public String getReviewerAvatar() { return reviewerAvatar != null ? reviewerAvatar : ""; }
    public void setReviewerAvatar(String reviewerAvatar) { this.reviewerAvatar = reviewerAvatar; }
}
