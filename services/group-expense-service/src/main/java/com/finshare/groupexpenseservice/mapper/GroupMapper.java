package com.finshare.groupexpenseservice.mapper;

import com.finshare.groupexpenseservice.dto.GroupDetailDto;
import com.finshare.groupexpenseservice.dto.GroupDto;
import com.finshare.groupexpenseservice.dto.SearchedUserDto;
import com.finshare.groupexpenseservice.model.Group;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper component for converting between Group entities and DTOs.
 */
@Component
public class GroupMapper {

    /**
     * Convert Group entity to GroupDto.
     *
     * @param group The group entity
     * @return GroupDto with group information
     */
    public GroupDto toGroupDto(Group group) {
        if (group == null) {
            return null;
        }
        
        return new GroupDto(
                group.getGroupId(),
                group.getGroupName(),
                group.getGroupImageUrl(),
                group.getMemberIds(),
                group.getCreatedBy(),
                group.getCreatedAt(),
                group.getUpdatedAt()
        );
    }

    /**
     * Convert Group entity to GroupDetailDto with member details.
     *
     * @param group The group entity
     * @param members List of member details
     * @return GroupDetailDto with group and member information
     */
    public GroupDetailDto toGroupDetailDto(Group group, List<SearchedUserDto> members) {
        if (group == null) {
            return null;
        }
        
        return new GroupDetailDto(
                group.getGroupId(),
                group.getGroupName(),
                group.getGroupImageUrl(),
                group.getMemberIds(),
                group.getCreatedBy(),
                group.getCreatedAt(),
                group.getUpdatedAt(),
                members
        );
    }
}