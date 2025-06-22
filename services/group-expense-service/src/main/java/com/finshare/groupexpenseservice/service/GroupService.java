package com.finshare.groupexpenseservice.service;

import com.finshare.groupexpenseservice.dto.*;
import com.finshare.groupexpenseservice.exception.GroupNotFoundException;
import com.finshare.groupexpenseservice.exception.UnauthorizedAccessException;
import com.finshare.groupexpenseservice.mapper.GroupMapper;
import com.finshare.groupexpenseservice.model.Group;
import com.finshare.groupexpenseservice.repository.GroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing group operations.
 */
@Service
@Transactional
public class GroupService {

    private static final Logger logger = LoggerFactory.getLogger(GroupService.class);

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private GroupMapper groupMapper;

    /**
     * Create a new group.
     *
     * @param createGroupDto The group creation data
     * @param authenticatedUserId The authenticated user's ID
     * @return GroupDto containing the created group information
     */
    public GroupDto createGroup(CreateGroupDto createGroupDto, String authenticatedUserId) {
        logger.debug("Creating new group '{}' by user: {}", createGroupDto.getGroupName(), authenticatedUserId);
        
        // Create new group with authenticated user as creator and first member
        Group group = new Group(createGroupDto.getGroupName(), createGroupDto.getGroupImageUrl(), authenticatedUserId);
        
        // Add additional members if phone numbers are provided
        if (createGroupDto.getMemberPhoneNumbers() != null && !createGroupDto.getMemberPhoneNumbers().isEmpty()) {
            for (String phoneNumber : createGroupDto.getMemberPhoneNumbers()) {
                Optional<SearchedUserDto> userOpt = userServiceClient.searchUserByPhoneNumber(phoneNumber, authenticatedUserId);
                if (userOpt.isPresent()) {
                    String userId = userOpt.get().getUserId();
                    if (!group.getMemberIds().contains(userId)) {
                        group.addMember(userId);
                        logger.debug("Added member {} to group", userId);
                    }
                } else {
                    logger.warn("User not found for phone number: {}", phoneNumber);
                }
            }
        }
        
        Group savedGroup = groupRepository.save(group);
        logger.info("Successfully created group '{}' with ID: {}", savedGroup.getGroupName(), savedGroup.getGroupId());
        
        return groupMapper.toGroupDto(savedGroup);
    }

    /**
     * Get group details by ID.
     *
     * @param groupId The group ID
     * @param authenticatedUserId The authenticated user's ID
     * @return GroupDetailDto containing the group information with member details
     */
    public GroupDetailDto getGroupDetails(String groupId, String authenticatedUserId) {
        logger.debug("Getting group details for groupId: {} by user: {}", groupId, authenticatedUserId);
        
        Group group = findGroupById(groupId);
        
        // Verify user is a member of the group
        if (!group.getMemberIds().contains(authenticatedUserId)) {
            throw new UnauthorizedAccessException("User is not a member of this group");
        }
        
        // Get member details from User Service
        List<SearchedUserDto> members = userServiceClient.getUserDetails(group.getMemberIds(), authenticatedUserId);
        
        logger.debug("Retrieved details for {} members of group: {}", members.size(), groupId);
        return groupMapper.toGroupDetailDto(group, members);
    }

    /**
     * Add a member to an existing group.
     *
     * @param groupId The group ID
     * @param addMemberDto The member addition data
     * @param authenticatedUserId The authenticated user's ID
     * @return GroupDto containing the updated group information
     */
    public GroupDto addMember(String groupId, AddMemberDto addMemberDto, String authenticatedUserId) {
        logger.debug("Adding member with phone {} to group: {} by user: {}", 
                    addMemberDto.getUserPhoneNumber(), groupId, authenticatedUserId);
        
        Group group = findGroupById(groupId);
        
        // Verify user is a member of the group
        if (!group.getMemberIds().contains(authenticatedUserId)) {
            throw new UnauthorizedAccessException("User is not a member of this group");
        }
        
        // Search for user by phone number
        Optional<SearchedUserDto> userOpt = userServiceClient.searchUserByPhoneNumber(
                addMemberDto.getUserPhoneNumber(), authenticatedUserId);
        
        if (userOpt.isEmpty()) {
            throw new GroupNotFoundException("User not found with phone number: " + addMemberDto.getUserPhoneNumber());
        }
        
        String newMemberId = userOpt.get().getUserId();
        
        // Add member if not already in group
        if (!group.getMemberIds().contains(newMemberId)) {
            group.addMember(newMemberId);
            Group savedGroup = groupRepository.save(group);
            logger.info("Successfully added member {} to group: {}", newMemberId, groupId);
            return groupMapper.toGroupDto(savedGroup);
        } else {
            logger.debug("User {} is already a member of group: {}", newMemberId, groupId);
            return groupMapper.toGroupDto(group);
        }
    }

    /**
     * Get all groups for a user.
     *
     * @param authenticatedUserId The authenticated user's ID
     * @return List of groups where the user is a member
     */
    public List<GroupDto> getUserGroups(String authenticatedUserId) {
        logger.debug("Getting all groups for user: {}", authenticatedUserId);
        
        List<Group> groups = groupRepository.findGroupsByMemberId(authenticatedUserId);
        logger.debug("Found {} groups for user: {}", groups.size(), authenticatedUserId);
        
        return groups.stream()
                .map(groupMapper::toGroupDto)
                .toList();
    }

    /**
     * Update group information.
     *
     * @param groupId The group ID
     * @param updateData The update data
     * @param authenticatedUserId The authenticated user's ID
     * @return GroupDto containing the updated group information
     */
    public GroupDto updateGroup(String groupId, CreateGroupDto updateData, String authenticatedUserId) {
        logger.debug("Updating group: {} by user: {}", groupId, authenticatedUserId);
        
        Group group = findGroupById(groupId);
        
        // Verify user is a member of the group
        if (!group.getMemberIds().contains(authenticatedUserId)) {
            throw new UnauthorizedAccessException("User is not a member of this group");
        }
        
        // Update fields if provided
        if (updateData.getGroupName() != null) {
            group.setGroupName(updateData.getGroupName());
        }
        if (updateData.getGroupImageUrl() != null) {
            group.setGroupImageUrl(updateData.getGroupImageUrl());
        }
        
        Group savedGroup = groupRepository.save(group);
        logger.info("Successfully updated group: {}", groupId);
        
        return groupMapper.toGroupDto(savedGroup);
    }

    /**
     * Remove a member from a group.
     *
     * @param groupId The group ID
     * @param memberUserId The user ID to remove
     * @param authenticatedUserId The authenticated user's ID
     * @return GroupDto containing the updated group information
     */
    public GroupDto removeMember(String groupId, String memberUserId, String authenticatedUserId) {
        logger.debug("Removing member {} from group: {} by user: {}", memberUserId, groupId, authenticatedUserId);
        
        Group group = findGroupById(groupId);
        
        // Verify user is a member of the group
        if (!group.getMemberIds().contains(authenticatedUserId)) {
            throw new UnauthorizedAccessException("User is not a member of this group");
        }
        
        // Remove member
        group.removeMember(memberUserId);
        Group savedGroup = groupRepository.save(group);
        
        logger.info("Successfully removed member {} from group: {}", memberUserId, groupId);
        return groupMapper.toGroupDto(savedGroup);
    }

    /**
     * Find group by ID or throw exception if not found.
     */
    private Group findGroupById(String groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("Group not found with ID: " + groupId));
    }

    /**
     * Check if user is a member of a group.
     *
     * @param groupId The group ID
     * @param userId The user ID
     * @return true if user is a member, false otherwise
     */
    public boolean isUserMemberOfGroup(String groupId, String userId) {
        return groupRepository.isUserMemberOfGroup(groupId, userId);
    }
}