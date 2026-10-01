package com.borrowbox.app.models;

import java.io.Serializable;

public class NotificationItem implements Serializable {
    private String id;
    private String type; // "request", "accepted", "returned", "due", "reminder"
    private String title;
    private String message;
    private String time;
    private boolean isRead;
    private String userEmail;

    public NotificationItem() {}

    public NotificationItem(String id, String type, String title, String message, String time, boolean isRead) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.message = message;
        this.time = time;
        this.isRead = isRead;
        this.userEmail = "nitya@bits.edu";
    }

    public NotificationItem(String id, String type, String title, String message, String time, boolean isRead, String userEmail) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.message = message;
        this.time = time;
        this.isRead = isRead;
        this.userEmail = userEmail;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
}
