package com.finshare.userservice.service;

import com.finshare.userservice.dto.SearchedUserDto;
import com.finshare.userservice.dto.UpdateUserDto;
import com.finshare.userservice.dto.UserDto;
import com.finshare.userservice.exception.UserNotFoundException;
import com.finshare.userservice.exception.PhoneNumberAlreadyExistsException;
import com.finshare.userservice.mapper.UserMapper;
import com.finshare.userservice.model.User;
import com.finshare.userservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

/**
 * Service class for managing user operations.
 * Handles user profile creation, updates, and searches.
 */
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    /**
     * Get user profile by user ID. Creates profile if it doesn't exist (just-in-time creation).
     *
     * @param userId The authenticated user's ID
     * @param phoneNumber The user's phone number (for profile creation)
     * @param email The user's email (for profile creation)
     * @param displayName The user's display name (for profile creation)
     * @return UserDto containing the user profile
     */
    public UserDto getUserProfile(String userId, String phoneNumber, String email, String displayName) {
        logger.debug("Getting user profile for userId: {}", userId);
        
        Optional<User> userOpt = userRepository.findById(userId);
        User user;
        
        if (userOpt.isPresent()) {
            user = userOpt.get();
            logger.debug("Found existing user profile for userId: {}", userId);
        } else {
            logger.info("User profile not found for userId: {}, creating new profile", userId);
            user = createUserProfile(userId, phoneNumber, email, displayName);
        }
        
        return userMapper.toUserDto(user);
    }

    /**
     * Update user profile with provided information.
     *
     * @param userId The authenticated user's ID
     * @param updateUserDto The update information
     * @return UserDto containing the updated user profile
     */
    public UserDto updateUserProfile(String userId, UpdateUserDto updateUserDto) {
        logger.debug("Updating user profile for userId: {}", userId);
        
        if (!updateUserDto.hasUpdates()) {
            logger.debug("No updates provided for userId: {}", userId);
            return getUserProfile(userId, null, null, null);
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new UserNotFoundException("User not found with ID: " + userId);
        }
        
        User existingUser = userOpt.get();
        
        // Apply updates
        if (updateUserDto.getDisplayName() != null) {
            existingUser.setDisplayName(updateUserDto.getDisplayName());
        }
        if (updateUserDto.getProfileImageUrl() != null) {
            existingUser.setProfileImageUrl(updateUserDto.getProfileImageUrl());
        }
        
        User savedUser = userRepository.save(existingUser);
        logger.info("Successfully updated user profile for userId: {}", userId);
        
        return userMapper.toUserDto(savedUser);
    }

    /**
     * Search for a user by phone number.
     *
     * @param phoneNumber The phone number to search for
     * @return SearchedUserDto containing the searched user information
     */
    public SearchedUserDto searchUserByPhoneNumber(String phoneNumber) {
        logger.debug("Searching for user by phone number: {}", phoneNumber);
        
        Optional<User> userOpt = userRepository.findByPhoneNumber(phoneNumber);
        if (userOpt.isEmpty()) {
            throw new UserNotFoundException("User not found with phone number: " + phoneNumber);
        }
        
        logger.debug("Successfully found user by phone number: {}", phoneNumber);
        return userMapper.toSearchedUserDto(userOpt.get());
    }

    /**
     * Create a new user profile (just-in-time creation).
     *
     * @param userId The user's ID from Firebase Auth
     * @param phoneNumber The user's phone number
     * @param email The user's email
     * @param displayName The user's display name
     * @return User containing the created user
     */
    private User createUserProfile(String userId, String phoneNumber, String email, String displayName) {
        logger.info("Creating new user profile for userId: {}", userId);
        
        // First check if phone number is already in use
        if (phoneNumber != null && userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new PhoneNumberAlreadyExistsException(
                    "Phone number already associated with another account: " + phoneNumber);
        }
        
        User newUser = new User(userId, phoneNumber, displayName, email, null);
        return userRepository.save(newUser);
    }
}