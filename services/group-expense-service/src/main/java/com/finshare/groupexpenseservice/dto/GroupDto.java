package com.finshare.groupexpenseservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

/**
 * Data Transfer Object for group information.
 */
public class GroupDto {

    @JsonProperty("groupId")
    private String groupId;

    @JsonProperty("groupName")
    private String groupName;

    @JsonProperty("groupImageUrl")
    private String groupImageUrl;

    @JsonProperty("memberIds")
    private List<String> memberIds;

    @JsonProperty("createdBy")
    private String createdBy;

    @JsonProperty("createdAt")
    private Instant createdAt;

    @JsonProperty("updatedAt")
    private Instant updatedAt;

    /**
     * Default constructor.
     */
    public GroupDto() {
    }

    /**
     * Constructor with all fields.
     */
    public GroupDto(String groupId, String groupName, String groupImageUrl, List<String> memberIds,
                    String createdBy, Instant createdAt, Instant updatedAt) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupImageUrl = groupImageUrl;
        this.memberIds = memberIds;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and setters

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

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

    public List<String> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(List<String> memberIds) {
        this.memberIds = memberIds;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}