package com.finshare.groupexpenseservice.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Group entity representing a financial group in the system.
 */
@Entity
@Table(name = "groups")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String groupId;

    @Column(nullable = false)
    private String groupName;

    private String groupImageUrl;

    @ElementCollection
    @CollectionTable(name = "group_members", joinColumns = @JoinColumn(name = "group_id"))
    @Column(name = "user_id")
    private List<String> memberIds = new ArrayList<>();

    @Column(nullable = false)
    private String createdBy;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant updatedAt;

    /**
     * Default constructor required by JPA.
     */
    public Group() {
    }

    /**
     * Constructor for creating a new group.
     */
    public Group(String groupName, String groupImageUrl, String createdBy) {
        this.groupName = groupName;
        this.groupImageUrl = groupImageUrl;
        this.createdBy = createdBy;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.memberIds = new ArrayList<>();
        this.memberIds.add(createdBy); // Creator is automatically a member
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
        this.updatedAt = Instant.now();
    }

    public String getGroupImageUrl() {
        return groupImageUrl;
    }

    public void setGroupImageUrl(String groupImageUrl) {
        this.groupImageUrl = groupImageUrl;
        this.updatedAt = Instant.now();
    }

    public List<String> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(List<String> memberIds) {
        this.memberIds = memberIds;
        this.updatedAt = Instant.now();
    }

    public void addMember(String userId) {
        if (!this.memberIds.contains(userId)) {
            this.memberIds.add(userId);
            this.updatedAt = Instant.now();
        }
    }

    public void removeMember(String userId) {
        this.memberIds.remove(userId);
        this.updatedAt = Instant.now();
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

    @Override
    public String toString() {
        return "Group{" +
                "groupId='" + groupId + '\'' +
                ", groupName='" + groupName + '\'' +
                ", memberIds=" + memberIds +
                ", createdBy='" + createdBy + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}