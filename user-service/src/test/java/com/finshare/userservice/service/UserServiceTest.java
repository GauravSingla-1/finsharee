package com.finshare.userservice.service;

import com.finshare.userservice.dto.SearchedUserDto;
import com.finshare.userservice.dto.UpdateUserDto;
import com.finshare.userservice.dto.UserDto;
import com.finshare.userservice.exception.UserNotFoundException;
import com.finshare.userservice.mapper.UserMapper;
import com.finshare.userservice.model.User;
import com.finshare.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserService.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    private UserService userService;

    private User testUser;
    private UserDto testUserDto;
    private SearchedUserDto testSearchedUserDto;

    @BeforeEach
    void setUp() {
        userService = new UserService();
        
        // Use reflection to inject mocks
        try {
            java.lang.reflect.Field repoField = UserService.class.getDeclaredField("userRepository");
            repoField.setAccessible(true);
            repoField.set(userService, userRepository);
            
            java.lang.reflect.Field mapperField = UserService.class.getDeclaredField("userMapper");
            mapperField.setAccessible(true);
            mapperField.set(userService, userMapper);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mocks", e);
        }

        // Test data
        testUser = new User("user123", "+1234567890", "John Doe", "john@example.com", "profile.jpg");
        testUser.setCreatedAt(Instant.now());

        testUserDto = new UserDto("user123", "+1234567890", "John Doe", "john@example.com", "profile.jpg", Instant.now());
        testSearchedUserDto = new SearchedUserDto("user123", "John Doe", "profile.jpg");
    }

    @Test
    void getUserProfile_ExistingUser_ReturnsUserDto() {
        // Arrange
        when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));
        when(userMapper.toUserDto(testUser)).thenReturn(testUserDto);

        // Act
        UserDto result = userService.getUserProfile("user123", null, null, null);

        // Assert
        assertEquals(testUserDto, result);
        verify(userRepository).findById("user123");
        verify(userMapper).toUserDto(testUser);
    }

    @Test
    void getUserProfile_NewUser_CreatesAndReturnsUser() {
        // Arrange
        when(userRepository.findById("user123")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toUserDto(testUser)).thenReturn(testUserDto);

        // Act
        UserDto result = userService.getUserProfile("user123", "+1234567890", "john@example.com", "John Doe");

        // Assert
        assertEquals(testUserDto, result);
        verify(userRepository).findById("user123");
        verify(userRepository).save(any(User.class));
        verify(userMapper).toUserDto(testUser);
    }

    @Test
    void updateUserProfile_ValidUpdate_ReturnsUpdatedUser() {
        // Arrange
        UpdateUserDto updateDto = new UpdateUserDto("Jane Doe", "newprofile.jpg");
        when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toUserDto(any(User.class))).thenReturn(testUserDto);

        // Act
        UserDto result = userService.updateUserProfile("user123", updateDto);

        // Assert
        assertEquals(testUserDto, result);
        verify(userRepository).findById("user123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUserProfile_UserNotFound_ThrowsException() {
        // Arrange
        UpdateUserDto updateDto = new UpdateUserDto("Jane Doe", null);
        when(userRepository.findById("user123")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            userService.updateUserProfile("user123", updateDto);
        });
    }

    @Test
    void searchUserByPhoneNumber_UserFound_ReturnsSearchedUserDto() {
        // Arrange
        when(userRepository.findByPhoneNumber("+1234567890")).thenReturn(Optional.of(testUser));
        when(userMapper.toSearchedUserDto(testUser)).thenReturn(testSearchedUserDto);

        // Act
        SearchedUserDto result = userService.searchUserByPhoneNumber("+1234567890");

        // Assert
        assertEquals(testSearchedUserDto, result);
        verify(userRepository).findByPhoneNumber("+1234567890");
        verify(userMapper).toSearchedUserDto(testUser);
    }

    @Test
    void searchUserByPhoneNumber_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findByPhoneNumber("+9999999999")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            userService.searchUserByPhoneNumber("+9999999999");
        });
    }
}