package com.finshare.groupexpenseservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object for adding a member to a group.
 */
public class AddMemberDto {

    @JsonProperty("userPhoneNumber")
    @NotBlank(message = "User phone number is required")
    private String userPhoneNumber;

    /**
     * Default constructor.
     */
    public AddMemberDto() {
    }

    /**
     * Constructor with all fields.
     */
    public AddMemberDto(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    // Getters and setters

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }
}