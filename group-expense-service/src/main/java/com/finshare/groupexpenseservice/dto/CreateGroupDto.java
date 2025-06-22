package com.finshare.groupexpenseservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Data Transfer Object for creating a new group.
 */
public class CreateGroupDto {

    @JsonProperty("groupName")
    @NotBlank(message = "Group name is required")
    @Size(min = 1, max = 100, message = "Group name must be between 1 and 100 characters")
    private String groupName;

    @JsonProperty("groupImageUrl")
    @Size(max = 500, message = "Group image URL must not exceed 500 characters")
    private String groupImageUrl;

    @JsonProperty("memberPhoneNumbers")
    private List<String> memberPhoneNumbers;

    /**
     * Default constructor.
     */
    public CreateGroupDto() {
    }

    /**
     * Constructor with all fields.
     */
    public CreateGroupDto(String groupName, String groupImageUrl, List<String> memberPhoneNumbers) {
        this.groupName = groupName;
        this.groupImageUrl = groupImageUrl;
        this.memberPhoneNumbers = memberPhoneNumbers;
    }

    // Getters and setters

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupImageUrl() {
        return groupImageUrl;
    }

    public void setGroupImageUrl(String groupImageUrl) {
        this.groupImageUrl = groupImageUrl;
    }

    public List<String> getMemberPhoneNumbers() {
        return memberPhoneNumbers;
    }

    public void setMemberPhoneNumbers(List<String> memberPhoneNumbers) {
        this.memberPhoneNumbers = memberPhoneNumbers;
    }
}