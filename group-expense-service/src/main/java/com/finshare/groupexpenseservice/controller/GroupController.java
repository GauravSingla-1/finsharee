package com.finshare.groupexpenseservice.controller;

import com.finshare.groupexpenseservice.dto.*;
import com.finshare.groupexpenseservice.service.GroupService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for group-related operations.
 */
@RestController
@RequestMapping("/groups")
@Validated
public class GroupController {

    private static final Logger logger = LoggerFactory.getLogger(GroupController.class);
    private static final String AUTHENTICATED_USER_HEADER = "X-Authenticated-User-ID";

    @Autowired
    private GroupService groupService;

    /**
     * Create a new group.
     *
     * @param authenticatedUserId The authenticated user's ID from the gateway
     * @param createGroupDto The group creation data
     * @return ResponseEntity containing the created group information
     */
    @PostMapping
    public ResponseEntity<GroupDto> createGroup(
            @RequestHeader(AUTHENTICATED_USER_HEADER) String authenticatedUserId,
            @Valid @RequestBody CreateGroupDto createGroupDto) {
        
        logger.debug("POST /groups - Creating group '{}' by user: {}", 
                    createGroupDto.getGroupName(), authenticatedUserId);
        
        GroupDto groupDto = groupService.createGroup(createGroupDto, authenticatedUserId);
        logger.debug("Successfully created group: {}", groupDto.getGroupId());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(groupDto);
    }

    /**
     * Get group details by ID.
     *
     * @param authenticatedUserId The authenticated user's ID from the gateway
     * @param groupId The group ID
     * @return ResponseEntity containing the group details
     */
    @GetMapping("/{groupId}")
    public ResponseEntity<GroupDetailDto> getGroupDetails(
            @RequestHeader(AUTHENTICATED_USER_HEADER) String authenticatedUserId,
            @PathVariable String groupId) {
        
        logger.debug("GET /groups/{} - user: {}", groupId, authenticatedUserId);
        
        GroupDetailDto groupDetailDto = groupService.getGroupDetails(groupId, authenticatedUserId);
        logger.debug("Successfully retrieved group details for: {}", groupId);
        
        return ResponseEntity.ok(groupDetailDto);
    }

    /**
     * Add a member to an existing group.
     *
     * @param authenticatedUserId The authenticated user's ID from the gateway
     * @param groupId The group ID
     * @param addMemberDto The member addition data
     * @return ResponseEntity containing the updated group information
     */
    @PostMapping("/{groupId}/members")
    public ResponseEntity<GroupDto> addMember(
            @RequestHeader(AUTHENTICATED_USER_HEADER) String authenticatedUserId,
            @PathVariable String groupId,
            @Valid @RequestBody AddMemberDto addMemberDto) {
        
        logger.debug("POST /groups/{}/members - Adding member by user: {}", groupId, authenticatedUserId);
        
        GroupDto groupDto = groupService.addMember(groupId, addMemberDto, authenticatedUserId);
        logger.debug("Successfully added member to group: {}", groupId);
        
        return ResponseEntity.ok(groupDto);
    }

    /**
     * Get all groups for the authenticated user.
     *
     * @param authenticatedUserId The authenticated user's ID from the gateway
     * @return ResponseEntity containing the list of user's groups
     */
    @GetMapping
    public ResponseEntity<List<GroupDto>> getUserGroups(
            @RequestHeader(AUTHENTICATED_USER_HEADER) String authenticatedUserId) {
        
        logger.debug("GET /groups - Getting groups for user: {}", authenticatedUserId);
        
        List<GroupDto> groups = groupService.getUserGroups(authenticatedUserId);
        logger.debug("Successfully retrieved {} groups for user: {}", groups.size(), authenticatedUserId);
        
        return ResponseEntity.ok(groups);
    }

    /**
     * Update group information.
     *
     * @param authenticatedUserId The authenticated user's ID from the gateway
     * @param groupId The group ID
     * @param updateData The update data
     * @return ResponseEntity containing the updated group information
     */
    @PutMapping("/{groupId}")
    public ResponseEntity<GroupDto> updateGroup(
            @RequestHeader(AUTHENTICATED_USER_HEADER) String authenticatedUserId,
            @PathVariable String groupId,
            @Valid @RequestBody CreateGroupDto updateData) {
        
        logger.debug("PUT /groups/{} - Updating group by user: {}", groupId, authenticatedUserId);
        
        GroupDto groupDto = groupService.updateGroup(groupId, updateData, authenticatedUserId);
        logger.debug("Successfully updated group: {}", groupId);
        
        return ResponseEntity.ok(groupDto);
    }

    /**
     * Remove a member from a group.
     *
     * @param authenticatedUserId The authenticated user's ID from the gateway
     * @param groupId The group ID
     * @param memberUserId The user ID to remove
     * @return ResponseEntity containing the updated group information
     */
    @DeleteMapping("/{groupId}/members/{memberUserId}")
    public ResponseEntity<GroupDto> removeMember(
            @RequestHeader(AUTHENTICATED_USER_HEADER) String authenticatedUserId,
            @PathVariable String groupId,
            @PathVariable String memberUserId) {
        
        logger.debug("DELETE /groups/{}/members/{} - Removing member by user: {}", 
                    groupId, memberUserId, authenticatedUserId);
        
        GroupDto groupDto = groupService.removeMember(groupId, memberUserId, authenticatedUserId);
        logger.debug("Successfully removed member from group: {}", groupId);
        
        return ResponseEntity.ok(groupDto);
    }
}