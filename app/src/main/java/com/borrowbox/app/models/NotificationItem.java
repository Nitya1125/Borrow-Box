package com.borrowbox.app.models;

import com.borrowbox.app.utils.DateTimeUtils;

import java.io.Serializable;

public class NotificationItem implements Serializable {
    private String id;
    private String userId;
    private String type; // "request", "accepted", "returned", "due", "reminder"
    private String refId;
    private String title;
    private String message;
    private long createdAt;
    private boolean isRead;

    public NotificationItem() {}

    public NotificationItem(String id, String userId, String type, String refId, String title, String message, long createdAt, boolean isRead) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.refId = refId != null ? refId : "";
        this.title = title;
        this.message = message;
        this.createdAt = createdAt;
        this.isRead = isRead;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId != null ? userId : ""; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserEmail() { return getUserId(); }
    public void setUserEmail(String userEmail) { this.userId = userEmail; }

    public String getType() { return type != null ? type : "reminder"; }
    public void setType(String type) { this.type = type; }

    public String getRefId() { return refId != null ? refId : ""; }
    public void setRefId(String refId) { this.refId = refId; }

    public String getTitle() { return title != null ? title : ""; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message != null ? message : ""; }
    public void setMessage(String message) { this.message = message; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public String getTime() {
        return DateTimeUtils.formatTimeAgo(createdAt > 0 ? createdAt : System.currentTimeMillis());
    }
}
