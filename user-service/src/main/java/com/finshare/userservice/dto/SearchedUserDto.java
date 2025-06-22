package com.finshare.userservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data Transfer Object for searched user information.
 * Contains only public, non-sensitive information for privacy.
 */
public class SearchedUserDto {

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("displayName")
    private String displayName;

    @JsonProperty("profileImageUrl")
    private String profileImageUrl;

    /**
     * Default constructor.
     */
    public SearchedUserDto() {
    }

    /**
     * Constructor with all fields.
     */
    public SearchedUserDto(String userId, String displayName, String profileImageUrl) {
        this.userId = userId;
        this.displayName = displayName;
        this.profileImageUrl = profileImageUrl;
    }

    // Getters and setters

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

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
}