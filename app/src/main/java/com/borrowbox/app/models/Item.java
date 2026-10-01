package com.borrowbox.app.models;

import java.io.Serializable;

public class Item implements Serializable {
    private String id;
    private String name;
    private String category;
    private String imageUrl;
    private String owner;
    private String ownerAvatar;
    private String ownerCollege;
    private double ownerRating;
    private boolean isAvailable;
    private String description;
    private String distance;
    private String maxDuration;
    private String pickupMethod;
    private String status; // "available", "borrowed", "inactive"
    private String currentBorrower;
    private String ownerEmail;

    public Item() {}

    public Item(String id, String name, String category, String imageUrl, String owner,
                String ownerAvatar, String ownerCollege, double ownerRating, boolean isAvailable,
                String description, String distance, String maxDuration, String pickupMethod) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.imageUrl = imageUrl;
        this.owner = owner;
        this.ownerAvatar = ownerAvatar;
        this.ownerCollege = ownerCollege;
        this.ownerRating = ownerRating;
        this.isAvailable = isAvailable;
        this.description = description;
        this.distance = distance;
        this.maxDuration = maxDuration;
        this.pickupMethod = pickupMethod;
        this.status = isAvailable ? "available" : "borrowed";
        this.ownerEmail = "nitya@bits.edu";
    }

    public Item(String id, String name, String category, String imageUrl, String owner,
                String ownerAvatar, String ownerCollege, double ownerRating, boolean isAvailable,
                String description, String distance, String maxDuration, String pickupMethod,
                String status, String currentBorrower) {
        this(id, name, category, imageUrl, owner, ownerAvatar, ownerCollege, ownerRating, isAvailable, description, distance, maxDuration, pickupMethod);
        this.status = status;
        this.currentBorrower = currentBorrower;
    }

    public Item(String id, String name, String category, String imageUrl, String owner,
                String ownerAvatar, String ownerCollege, double ownerRating, boolean isAvailable,
                String description, String distance, String maxDuration, String pickupMethod,
                String status, String currentBorrower, String ownerEmail) {
        this(id, name, category, imageUrl, owner, ownerAvatar, ownerCollege, ownerRating, isAvailable, description, distance, maxDuration, pickupMethod);
        this.status = status;
        this.currentBorrower = currentBorrower;
        this.ownerEmail = ownerEmail;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }

    public String getOwnerAvatar() { return ownerAvatar; }
    public void setOwnerAvatar(String ownerAvatar) { this.ownerAvatar = ownerAvatar; }

    public String getOwnerCollege() { return ownerCollege; }
    public void setOwnerCollege(String ownerCollege) { this.ownerCollege = ownerCollege; }

    public double getOwnerRating() { return ownerRating; }
    public void setOwnerRating(double ownerRating) { this.ownerRating = ownerRating; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDistance() { return distance; }
    public void setDistance(String distance) { this.distance = distance; }

    public String getMaxDuration() { return maxDuration; }
    public void setMaxDuration(String maxDuration) { this.maxDuration = maxDuration; }

    public String getPickupMethod() { return pickupMethod; }
    public void setPickupMethod(String pickupMethod) { this.pickupMethod = pickupMethod; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCurrentBorrower() { return currentBorrower; }
    public void setCurrentBorrower(String currentBorrower) { this.currentBorrower = currentBorrower; }

    public String getOwnerEmail() { return ownerEmail != null ? ownerEmail : "nitya@bits.edu"; }
    public void setOwnerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; }
}
