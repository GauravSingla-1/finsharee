package com.finshare.userservice.controller;

import com.finshare.userservice.dto.SearchedUserDto;
import com.finshare.userservice.dto.UpdateUserDto;
import com.finshare.userservice.dto.UserDto;
import com.finshare.userservice.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for user-related operations.
 * Handles user profile management and search functionality.
 */
@RestController
@RequestMapping("/users")
@Validated
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private static final String AUTHENTICATED_USER_HEADER = "X-Authenticated-User-ID";

    @Autowired
    private UserService userService;

    /**
     * Get the authenticated user's profile.
     * Creates a new profile if it doesn't exist (just-in-time creation).
     *
     * @param authenticatedUserId The authenticated user's ID from the gateway
     * @param phoneNumber Optional phone number for profile creation
     * @param email Optional email for profile creation
     * @param displayName Optional display name for profile creation
     * @return ResponseEntity containing the user profile
     */
    @GetMapping("/me")
    public ResponseEntity<UserDto> getUserProfile(
            @RequestHeader(AUTHENTICATED_USER_HEADER) String authenticatedUserId,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String displayName) {
        
        logger.debug("GET /users/me - userId: {}", authenticatedUserId);
        
        UserDto userDto = userService.getUserProfile(authenticatedUserId, phoneNumber, email, displayName);
        logger.debug("Successfully returned user profile for userId: {}", authenticatedUserId);
        
        return ResponseEntity.ok(userDto);
    }

    /**
     * Update the authenticated user's profile.
     *
     * @param authenticatedUserId The authenticated user's ID from the gateway
     * @param updateUserDto The update information
     * @return ResponseEntity containing the updated user profile
     */
    @PutMapping("/me")
    public ResponseEntity<UserDto> updateUserProfile(
            @RequestHeader(AUTHENTICATED_USER_HEADER) String authenticatedUserId,
            @Valid @RequestBody UpdateUserDto updateUserDto) {
        
        logger.debug("PUT /users/me - userId: {}", authenticatedUserId);
        
        UserDto userDto = userService.updateUserProfile(authenticatedUserId, updateUserDto);
        logger.debug("Successfully updated user profile for userId: {}", authenticatedUserId);
        
        return ResponseEntity.ok(userDto);
    }

    /**
     * Search for a user by phone number.
     *
     * @param authenticatedUserId The authenticated user's ID from the gateway (for logging)
     * @param phoneNumber The phone number to search for
     * @return ResponseEntity containing the searched user information
     */
    @GetMapping("/search")
    public ResponseEntity<SearchedUserDto> searchUserByPhoneNumber(
            @RequestHeader(AUTHENTICATED_USER_HEADER) String authenticatedUserId,
            @RequestParam("phone") @NotBlank(message = "Phone number is required") String phoneNumber) {
        
        logger.debug("GET /users/search - phone: {} requested by userId: {}", phoneNumber, authenticatedUserId);
        
        SearchedUserDto searchedUserDto = userService.searchUserByPhoneNumber(phoneNumber);
        logger.debug("Successfully found user by phone number: {}", phoneNumber);
        
        return ResponseEntity.ok(searchedUserDto);
    }
}