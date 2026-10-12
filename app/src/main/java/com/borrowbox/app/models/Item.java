package com.borrowbox.app.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Item implements Serializable {
    private String id;
    private String ownerId;
    private String categoryId;
    private String name;
    private String description;
    private String photoPath; // Primary or comma-separated list of paths
    private String status; // "available", "borrowed", "inactive"
    private int maxDurationDays;
    private String pickupLocation;
    private long createdAt;

    // Joined / Resolved Owner fields
    private String owner;
    private String ownerAvatar;
    private String ownerCollege;
    private double ownerRating;
    private String ownerEmail;
    private String currentBorrower;

    public Item() {
        this.status = "available";
        this.maxDurationDays = 3;
        this.photoPath = "";
        this.pickupLocation = "";
    }

    public Item(String id, String ownerId, String categoryId, String name, String description,
                String photoPath, String status, int maxDurationDays, String pickupLocation, long createdAt) {
        this.id = id;
        this.ownerId = ownerId;
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.photoPath = photoPath != null ? photoPath : "";
        this.status = status != null ? status : "available";
        this.maxDurationDays = maxDurationDays;
        this.pickupLocation = pickupLocation != null ? pickupLocation : "";
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public String getCategory() { return categoryId; }
    public void setCategory(String category) { this.categoryId = category; }

    public String getName() { return name != null ? name : ""; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description != null ? description : ""; }
    public void setDescription(String description) { this.description = description; }

    public String getPhotoPath() { return photoPath != null ? photoPath : ""; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }

    public String getImageUrl() { return getPrimaryPhotoPath(); }
    public void setImageUrl(String imageUrl) { this.photoPath = imageUrl; }

    public String getPrimaryPhotoPath() {
        if (photoPath == null || photoPath.trim().isEmpty()) return "";
        if (photoPath.contains(",")) {
            return photoPath.split(",")[0].trim();
        }
        return photoPath;
    }

    public List<String> getAllPhotoPaths() {
        List<String> list = new ArrayList<>();
        if (photoPath == null || photoPath.trim().isEmpty()) return list;
        String[] parts = photoPath.split(",");
        for (String p : parts) {
            String trimmed = p.trim();
            if (!trimmed.isEmpty()) list.add(trimmed);
        }
        return list;
    }

    public void setPhotoPaths(List<String> paths) {
        if (paths == null || paths.isEmpty()) {
            this.photoPath = "";
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < paths.size(); i++) {
                sb.append(paths.get(i));
                if (i < paths.size() - 1) sb.append(",");
            }
            this.photoPath = sb.toString();
        }
    }

    public String getStatus() { return status != null ? status : "available"; }
    public void setStatus(String status) { this.status = status; }

    public boolean isAvailable() {
        return "available".equalsIgnoreCase(status);
    }

    public void setAvailable(boolean available) {
        this.status = available ? "available" : "borrowed";
    }

    public int getMaxDurationDays() { return maxDurationDays > 0 ? maxDurationDays : 3; }
    public void setMaxDurationDays(int maxDurationDays) { this.maxDurationDays = maxDurationDays; }

    public String getMaxDuration() {
        return maxDurationDays + (maxDurationDays == 1 ? " day" : " days");
    }

    public void setMaxDuration(String durationStr) {
        if (durationStr != null) {
            try {
                String num = durationStr.replaceAll("[^0-9]", "");
                if (!num.isEmpty()) {
                    this.maxDurationDays = Integer.parseInt(num);
                }
            } catch (Exception ignored) {}
        }
    }

    public String getPickupLocation() { return pickupLocation != null ? pickupLocation : ""; }
    public void setPickupLocation(String pickupLocation) { this.pickupLocation = pickupLocation; }

    public String getPickupMethod() { return getPickupLocation(); }
    public void setPickupMethod(String method) { setPickupLocation(method); }

    public String getDistance() {
        return pickupLocation != null && !pickupLocation.isEmpty() ? pickupLocation : "Campus Pickup";
    }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    // Joined Owner Info
    public String getOwner() { return owner != null ? owner : ""; }
    public void setOwner(String owner) { this.owner = owner; }

    public String getOwnerAvatar() { return ownerAvatar != null ? ownerAvatar : ""; }
    public void setOwnerAvatar(String ownerAvatar) { this.ownerAvatar = ownerAvatar; }

    public String getOwnerCollege() { return ownerCollege != null ? ownerCollege : ""; }
    public void setOwnerCollege(String ownerCollege) { this.ownerCollege = ownerCollege; }

    public double getOwnerRating() { return ownerRating; }
    public void setOwnerRating(double ownerRating) { this.ownerRating = ownerRating; }

    public String getOwnerEmail() { return ownerEmail != null ? ownerEmail : ""; }
    public void setOwnerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; }

    public String getCurrentBorrower() { return currentBorrower; }
    public void setCurrentBorrower(String currentBorrower) { this.currentBorrower = currentBorrower; }
}
