package com.example.CGI_Restaurant.mappers;

import com.example.CGI_Restaurant.domain.dtos.UserInfoDto;
import com.example.CGI_Restaurant.domain.entities.User;
import com.example.CGI_Restaurant.domain.entities.UserRoleEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Import(com.example.CGI_Restaurant.TestMailConfig.class)
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Nested
    @DisplayName("toUserInfoDto")
    class ToUserInfoDto {

        @Test
        @DisplayName("maps user entity to UserInfoDto with all fields")
        void mapsUserToUserInfoDto() {
            UUID id = UUID.randomUUID();
            LocalDateTime now = LocalDateTime.now();
            User user = User.builder()
                    .id(id)
                    .name("John Doe")
                    .email("john@example.com")
                    .passwordHash("hashed")
                    .role(UserRoleEnum.CUSTOMER)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();

            UserInfoDto dto = userMapper.toUserInfoDto(user);

            assertNotNull(dto);
            assertEquals(id, dto.getId());
            assertEquals("John Doe", dto.getName());
            assertEquals("john@example.com", dto.getEmail());
        }

        @Test
        @DisplayName("maps user with ADMIN role")
        void mapsAdminUser() {
            User user = User.builder()
                    .id(UUID.randomUUID())
                    .name("Admin")
                    .email("admin@restaurant.com")
                    .passwordHash("secret")
                    .role(UserRoleEnum.ADMIN)
                    .build();

            UserInfoDto dto = userMapper.toUserInfoDto(user);

            assertNotNull(dto);
            assertEquals("Admin", dto.getName());
            assertEquals("admin@restaurant.com", dto.getEmail());
        }

        @Test
        @DisplayName("returns null when user is null")
        void returnsNullWhenUserIsNull() {
            UserInfoDto dto = userMapper.toUserInfoDto(null);
            assertNull(dto);
        }
    }
}
