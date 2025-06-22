package com.finshare.groupexpenseservice.repository;

import com.finshare.groupexpenseservice.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Group entities.
 */
@Repository
public interface GroupRepository extends JpaRepository<Group, String> {

    /**
     * Find all groups where the user is a member.
     *
     * @param userId The user ID to search for
     * @return List of groups where the user is a member
     */
    @Query("SELECT g FROM Group g JOIN g.memberIds m WHERE m = :userId")
    List<Group> findGroupsByMemberId(@Param("userId") String userId);

    /**
     * Check if a user is a member of a specific group.
     *
     * @param groupId The group ID
     * @param userId The user ID
     * @return true if the user is a member, false otherwise
     */
    @Query("SELECT COUNT(g) > 0 FROM Group g JOIN g.memberIds m WHERE g.groupId = :groupId AND m = :userId")
    boolean isUserMemberOfGroup(@Param("groupId") String groupId, @Param("userId") String userId);
}