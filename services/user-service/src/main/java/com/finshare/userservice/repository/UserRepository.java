package com.finshare.userservice.repository;

import com.finshare.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entities.
 * Extends JpaRepository for standard database operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

    /**
     * Find a user by their phone number.
     * Phone numbers are indexed for fast lookups.
     *
     * @param phoneNumber The phone number to search for
     * @return Optional containing the user if found, empty if not found
     */
    Optional<User> findByPhoneNumber(String phoneNumber);

    /**
     * Check if a user exists with the given phone number.
     *
     * @param phoneNumber The phone number to check
     * @return true if exists, false otherwise
     */
    boolean existsByPhoneNumber(String phoneNumber);
}