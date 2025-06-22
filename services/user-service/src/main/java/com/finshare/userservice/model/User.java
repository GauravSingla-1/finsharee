package com.finshare.userservice.model;

import jakarta.persistence.*;

import java.time.Instant;

/**
 * User entity representing a user profile.
 * The ID corresponds to the Firebase Auth UID.
 */
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_phone_number", columnList = "phoneNumber", unique = true)
})
public class User {

    @Id
    private String userId;

    @Column(unique = true)
    private String phoneNumber;
    
    private String displayName;
    private String email;
    private String profileImageUrl;
    private Instant createdAt;

    /**
     * Default constructor required by Firestore.
     */
    public User() {
    }

    /**
     * Constructor for creating a new user profile.
     */
    public User(String userId, String phoneNumber, String displayName, String email, String profileImageUrl) {
        this.userId = userId;
        this.phoneNumber = phoneNumber;
        this.displayName = displayName;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.createdAt = Instant.now();
    }

    // Getters and setters

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", displayName='" + displayName + '\'' +
                ", email='" + email + '\'' +
                ", profileImageUrl='" + profileImageUrl + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}