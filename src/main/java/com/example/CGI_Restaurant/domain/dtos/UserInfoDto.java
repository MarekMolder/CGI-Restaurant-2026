package com.example.CGI_Restaurant.domain.dtos;

import com.example.CGI_Restaurant.domain.entities.UserRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Safe user info for API responses (e.g. on booking: who made the booking).
 * Does not expose password or other sensitive data.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDto {
    private UUID id;
    private String name;
    private String email;
    private UserRoleEnum role;
}
