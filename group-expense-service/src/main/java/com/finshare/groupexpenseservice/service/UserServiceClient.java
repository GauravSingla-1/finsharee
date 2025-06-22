package com.finshare.groupexpenseservice.service;

import com.finshare.groupexpenseservice.dto.SearchedUserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Optional;

/**
 * Client service for communicating with the User Service.
 */
@Service
public class UserServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceClient.class);

    private final WebClient webClient;

    public UserServiceClient(@Value("${user-service.base-url:http://localhost:8001}") String userServiceBaseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(userServiceBaseUrl)
                .build();
    }

    /**
     * Search for a user by phone number.
     *
     * @param phoneNumber The phone number to search for
     * @param authenticatedUserId The ID of the user making the request (for authentication header)
     * @return Optional containing the user if found
     */
    public Optional<SearchedUserDto> searchUserByPhoneNumber(String phoneNumber, String authenticatedUserId) {
        try {
            logger.debug("Searching for user by phone number: {}", phoneNumber);
            
            SearchedUserDto user = webClient.get()
                    .uri("/users/search?phone={phone}", phoneNumber)
                    .header("X-Authenticated-User-ID", authenticatedUserId)
                    .retrieve()
                    .bodyToMono(SearchedUserDto.class)
                    .block();
            
            logger.debug("Found user: {}", user != null ? user.getUserId() : "null");
            return Optional.ofNullable(user);
            
        } catch (WebClientResponseException.NotFound e) {
            logger.debug("User not found for phone number: {}", phoneNumber);
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Error searching for user by phone number: {}", phoneNumber, e);
            throw new RuntimeException("Failed to search for user", e);
        }
    }

    /**
     * Get user details for multiple user IDs.
     * This method makes individual requests for each user ID since the User Service
     * doesn't have a batch endpoint yet.
     *
     * @param userIds List of user IDs to get details for
     * @param authenticatedUserId The ID of the user making the request
     * @return List of user details
     */
    public List<SearchedUserDto> getUserDetails(List<String> userIds, String authenticatedUserId) {
        logger.debug("Getting user details for {} users", userIds.size());
        
        return userIds.stream()
                .map(userId -> getUserDetails(userId, authenticatedUserId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    /**
     * Get user details for a single user ID.
     *
     * @param userId The user ID to get details for
     * @param authenticatedUserId The ID of the user making the request
     * @return Optional containing the user details if found
     */
    public Optional<SearchedUserDto> getUserDetails(String userId, String authenticatedUserId) {
        try {
            // For now, we'll create a mock response since the User Service 
            // doesn't have an endpoint to get user details by ID
            // In production, this would call a proper endpoint
            SearchedUserDto user = new SearchedUserDto(userId, "User " + userId.substring(0, 8), null);
            return Optional.of(user);
            
        } catch (Exception e) {
            logger.error("Error getting user details for ID: {}", userId, e);
            return Optional.empty();
        }
    }
}