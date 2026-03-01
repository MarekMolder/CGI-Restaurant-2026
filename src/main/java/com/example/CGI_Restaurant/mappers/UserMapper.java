package com.example.CGI_Restaurant.mappers;

import com.example.CGI_Restaurant.domain.dtos.UserInfoDto;
import com.example.CGI_Restaurant.domain.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper from User entity to UserInfoDto (id, name, email). Used by BookingMapper and auth responses.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    /** Maps a User to a minimal DTO for API responses. */
    UserInfoDto toUserInfoDto(User user);
}
