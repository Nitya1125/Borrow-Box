package com.borrowbox.app.models;

import java.io.Serializable;

public class Transaction implements Serializable {
    private String id;
    private Item item;
    private String person;
    private String personAvatar;
    private String status; // "pending", "approved", "borrowed", "returned", "rejected", "overdue"
    private String borrowDate;
    private String returnDate;
    private String type; // "borrowed", "lent"
    private String borrowerEmail;
    private String ownerEmail;
    private String requestMessage;
    private float ratingScore;
    private String reviewComment;

    public Transaction() {}

    public Transaction(String id, Item item, String person, String personAvatar,
                       String status, String borrowDate, String returnDate, String type) {
        this.id = id;
        this.item = item;
        this.person = person;
        this.personAvatar = personAvatar;
        this.status = status;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.type = type;
        this.borrowerEmail = "nitya@bits.edu";
        this.ownerEmail = (item != null && item.getOwnerEmail() != null) ? item.getOwnerEmail() : "priya@iitd.ac.in";
        this.requestMessage = "";
        this.ratingScore = 5.0f;
        this.reviewComment = "";
    }

    public Transaction(String id, Item item, String person, String personAvatar,
                       String status, String borrowDate, String returnDate, String type,
                       String borrowerEmail, String ownerEmail, String requestMessage,
                       float ratingScore, String reviewComment) {
        this.id = id;
        this.item = item;
        this.person = person;
        this.personAvatar = personAvatar;
        this.status = status;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
        this.type = type;
        this.borrowerEmail = borrowerEmail;
        this.ownerEmail = ownerEmail;
        this.requestMessage = requestMessage;
        this.ratingScore = ratingScore;
        this.reviewComment = reviewComment;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Item getItem() { return item; }
    public void setItem(Item item) { this.item = item; }

    public String getPerson() { return person; }
    public void setPerson(String person) { this.person = person; }

    public String getPersonAvatar() { return personAvatar; }
    public void setPersonAvatar(String personAvatar) { this.personAvatar = personAvatar; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getBorrowDate() { return borrowDate; }
    public void setBorrowDate(String borrowDate) { this.borrowDate = borrowDate; }

    public String getReturnDate() { return returnDate; }
    public void setReturnDate(String returnDate) { this.returnDate = returnDate; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getBorrowerEmail() { return borrowerEmail; }
    public void setBorrowerEmail(String borrowerEmail) { this.borrowerEmail = borrowerEmail; }

    public String getOwnerEmail() { return ownerEmail; }
    public void setOwnerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; }

    public String getRequestMessage() { return requestMessage; }
    public void setRequestMessage(String requestMessage) { this.requestMessage = requestMessage; }

    public float getRatingScore() { return ratingScore; }
    public void setRatingScore(float ratingScore) { this.ratingScore = ratingScore; }

    public String getReviewComment() { return reviewComment; }
    public void setReviewComment(String reviewComment) { this.reviewComment = reviewComment; }
}
