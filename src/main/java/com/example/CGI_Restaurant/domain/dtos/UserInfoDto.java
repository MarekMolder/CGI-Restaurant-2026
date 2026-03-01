package com.example.CGI_Restaurant.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/** DTO with minimal user info (id, name, email) for API responses. */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDto {
    private UUID id;
    private String name;
    private String email;
}
