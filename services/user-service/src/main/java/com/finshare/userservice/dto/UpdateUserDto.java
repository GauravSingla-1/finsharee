package com.finshare.userservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for updating user profile information.
 * All fields are optional for partial updates.
 */
public class UpdateUserDto {

    @JsonProperty("displayName")
    @Size(min = 1, max = 100, message = "Display name must be between 1 and 100 characters")
    private String displayName;

    @JsonProperty("profileImageUrl")
    @Size(max = 500, message = "Profile image URL must not exceed 500 characters")
    private String profileImageUrl;

    /**
     * Default constructor.
     */
    public UpdateUserDto() {
    }

    /**
     * Constructor with all fields.
     */
    public UpdateUserDto(String displayName, String profileImageUrl) {
        this.displayName = displayName;
        this.profileImageUrl = profileImageUrl;
    }

    // Getters and setters

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    /**
     * Check if any field is provided for update.
     */
    public boolean hasUpdates() {
        return displayName != null || profileImageUrl != null;
    }
}