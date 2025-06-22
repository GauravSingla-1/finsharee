package com.finshare.userservice.mapper;

import com.finshare.userservice.dto.SearchedUserDto;
import com.finshare.userservice.dto.UserDto;
import com.finshare.userservice.model.User;
import org.springframework.stereotype.Component;

/**
 * Mapper component for converting between User entities and DTOs.
 */
@Component
public class UserMapper {

    /**
     * Convert User entity to UserDto.
     *
     * @param user The user entity
     * @return UserDto with all user information
     */
    public UserDto toUserDto(User user) {
        if (user == null) {
            return null;
        }
        
        return new UserDto(
                user.getUserId(),
                user.getPhoneNumber(),
                user.getDisplayName(),
                user.getEmail(),
                user.getProfileImageUrl(),
                user.getCreatedAt()
        );
    }

    /**
     * Convert User entity to SearchedUserDto (limited information for privacy).
     *
     * @param user The user entity
     * @return SearchedUserDto with only public information
     */
    public SearchedUserDto toSearchedUserDto(User user) {
        if (user == null) {
            return null;
        }
        
        return new SearchedUserDto(
                user.getUserId(),
                user.getDisplayName(),
                user.getProfileImageUrl()
        );
    }
}