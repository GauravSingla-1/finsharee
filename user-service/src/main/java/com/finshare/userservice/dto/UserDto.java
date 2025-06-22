package com.finshare.userservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

/**
 * Data Transfer Object for complete user information.
 * Used for returning full user profile data.
 */
public class UserDto {

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("phoneNumber")
    private String phoneNumber;

    @JsonProperty("displayName")
    private String displayName;

    @JsonProperty("email")
    private String email;

    @JsonProperty("profileImageUrl")
    private String profileImageUrl;

    @JsonProperty("createdAt")
    private Instant createdAt;

    /**
     * Default constructor.
     */
    public UserDto() {
    }

    /**
     * Constructor with all fields.
     */
    public UserDto(String userId, String phoneNumber, String displayName, 
                   String email, String profileImageUrl, Instant createdAt) {
        this.userId = userId;
        this.phoneNumber = phoneNumber;
        this.displayName = displayName;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.createdAt = createdAt;
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
}