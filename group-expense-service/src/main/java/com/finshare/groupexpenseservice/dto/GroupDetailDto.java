package com.finshare.groupexpenseservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

/**
 * Data Transfer Object for detailed group information including member details.
 */
public class GroupDetailDto extends GroupDto {

    @JsonProperty("members")
    private List<SearchedUserDto> members;

    /**
     * Default constructor.
     */
    public GroupDetailDto() {
        super();
    }

    /**
     * Constructor with all fields.
     */
    public GroupDetailDto(String groupId, String groupName, String groupImageUrl, List<String> memberIds,
                         String createdBy, Instant createdAt, Instant updatedAt, List<SearchedUserDto> members) {
        super(groupId, groupName, groupImageUrl, memberIds, createdBy, createdAt, updatedAt);
        this.members = members;
    }

    // Getters and setters

    public List<SearchedUserDto> getMembers() {
        return members;
    }

    public void setMembers(List<SearchedUserDto> members) {
        this.members = members;
    }
}