package com.borrowbox.app.models;

import com.borrowbox.app.utils.DateTimeUtils;

import java.io.Serializable;

public class Transaction implements Serializable {
    private String id;
    private String itemId;
    private String borrowerId;
    private String ownerId;
    private String status; // "pending", "approved", "borrowed", "returned", "rejected", "cancelled", "overdue"
    private long requestedAt;
    private long dueAt;
    private long returnedAt;
    private String message;
    private String qrToken;

    // Joined / Resolved fields
    private Item item;
    private String person;
    private String personAvatar;
    private String borrowerEmail;
    private String ownerEmail;
    private String type; // "borrowed" or "lent" relative to current user

    // Legacy review fields
    private float ratingScore;
    private String reviewComment;

    public Transaction() {
        this.status = "pending";
        this.ratingScore = 5.0f;
        this.reviewComment = "";
    }

    public Transaction(String id, String itemId, String borrowerId, String ownerId,
                       String status, long requestedAt, long dueAt, long returnedAt,
                       String message, String qrToken) {
        this.id = id;
        this.itemId = itemId;
        this.borrowerId = borrowerId;
        this.ownerId = ownerId;
        this.status = status;
        this.requestedAt = requestedAt;
        this.dueAt = dueAt;
        this.returnedAt = returnedAt;
        this.message = message != null ? message : "";
        this.qrToken = qrToken != null ? qrToken : "";
        this.ratingScore = 5.0f;
        this.reviewComment = "";
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }

    public String getBorrowerId() { return borrowerId; }
    public void setBorrowerId(String borrowerId) { this.borrowerId = borrowerId; }

    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

    public String getStatus() {
        // Dynamically compute overdue if borrowed and dueAt has passed
        if ("borrowed".equalsIgnoreCase(status) && dueAt > 0 && DateTimeUtils.isOverdue(dueAt)) {
            return "overdue";
        }
        return status != null ? status : "pending";
    }

    public void setStatus(String status) { this.status = status; }

    public long getRequestedAt() { return requestedAt; }
    public void setRequestedAt(long requestedAt) { this.requestedAt = requestedAt; }

    public long getDueAt() { return dueAt; }
    public void setDueAt(long dueAt) { this.dueAt = dueAt; }

    public long getReturnedAt() { return returnedAt; }
    public void setReturnedAt(long returnedAt) { this.returnedAt = returnedAt; }

    public String getMessage() { return message != null ? message : ""; }
    public void setMessage(String message) { this.message = message; }

    public String getQrToken() { return qrToken != null ? qrToken : ""; }
    public void setQrToken(String qrToken) { this.qrToken = qrToken; }

    // Joined object & view helpers
    public Item getItem() { return item; }
    public void setItem(Item item) {
        this.item = item;
        if (item != null) {
            this.itemId = item.getId();
        }
    }

    public String getPerson() { return person != null ? person : ""; }
    public void setPerson(String person) { this.person = person; }

    public String getPersonAvatar() { return personAvatar != null ? personAvatar : ""; }
    public void setPersonAvatar(String personAvatar) { this.personAvatar = personAvatar; }

    public String getBorrowerEmail() { return borrowerEmail != null ? borrowerEmail : ""; }
    public void setBorrowerEmail(String borrowerEmail) { this.borrowerEmail = borrowerEmail; }

    public String getOwnerEmail() { return ownerEmail != null ? ownerEmail : ""; }
    public void setOwnerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; }

    public String getType() { return type != null ? type : "borrowed"; }
    public void setType(String type) { this.type = type; }

    public String getBorrowDate() {
        return DateTimeUtils.formatDate(requestedAt > 0 ? requestedAt : System.currentTimeMillis());
    }

    public String getReturnDate() {
        return DateTimeUtils.formatDate(dueAt > 0 ? dueAt : System.currentTimeMillis());
    }

    public float getRatingScore() { return ratingScore; }
    public void setRatingScore(float ratingScore) { this.ratingScore = ratingScore; }

    public String getReviewComment() { return reviewComment != null ? reviewComment : ""; }
    public void setReviewComment(String reviewComment) { this.reviewComment = reviewComment; }

    public String getFormattedDisplayId() {
        return "BB-" + (id != null && id.length() >= 4 ? id.substring(id.length() - 4).toUpperCase() : "TX") + "-" + DateTimeUtils.formatMonthYear(requestedAt > 0 ? requestedAt : System.currentTimeMillis()).replace(" ", "");
    }

    public boolean isOverdue() {
        return "borrowed".equalsIgnoreCase(status) && dueAt > 0 && System.currentTimeMillis() > dueAt;
    }
}
