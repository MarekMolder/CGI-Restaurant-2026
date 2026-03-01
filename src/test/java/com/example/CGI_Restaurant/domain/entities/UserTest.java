package com.example.CGI_Restaurant.domain.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Nested
    @DisplayName("Builder")
    class BuilderTests {

        @Test
        @DisplayName("Builder creates user with all attributes")
        void builderCreatesUserWithAllFields() {
            UUID id = UUID.randomUUID();
            LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
            LocalDateTime updatedAt = LocalDateTime.now();

            User user = User.builder()
                    .id(id)
                    .name("Admin User")
                    .email("admin@restaurant.ee")
                    .passwordHash("hashedPassword123")
                    .role(UserRoleEnum.ADMIN)
                    .bookings(new ArrayList<>())
                    .createdAt(createdAt)
                    .updatedAt(updatedAt)
                    .build();

            assertNotNull(user);
            assertEquals(id, user.getId());
            assertEquals("Admin User", user.getName());
            assertEquals("admin@restaurant.ee", user.getEmail());
            assertEquals("hashedPassword123", user.getPasswordHash());
            assertEquals(UserRoleEnum.ADMIN, user.getRole());
            assertNotNull(user.getBookings());
            assertTrue(user.getBookings().isEmpty());
            assertEquals(createdAt, user.getCreatedAt());
            assertEquals(updatedAt, user.getUpdatedAt());
        }

        @Test
        @DisplayName("Builder without ID (JPA generates it)")
        void builderWithoutId() {
            User user = User.builder()
                    .name("Customer")
                    .email("customer@test.ee")
                    .passwordHash("hash")
                    .role(UserRoleEnum.CUSTOMER)
                    .build();

            assertNull(user.getId());
            assertEquals("Customer", user.getName());
            assertEquals(UserRoleEnum.CUSTOMER, user.getRole());
        }
    }

    @Nested
    @DisplayName("No-args constructor and setters")
    class ConstructorAndSetters {

        @Test
        @DisplayName("No-args constructor creates empty user")
        void noArgsConstructorCreatesEmptyUser() {
            User user = new User();

            assertNotNull(user);
            assertNull(user.getId());
            assertNull(user.getName());
            assertNull(user.getEmail());
            assertNull(user.getRole());
            assertNotNull(user.getBookings());
            assertTrue(user.getBookings().isEmpty());
        }

        @Test
        @DisplayName("Setters and getters work")
        void settersAndGettersWork() {
            User user = new User();
            UUID id = UUID.randomUUID();
            LocalDateTime now = LocalDateTime.now();

            user.setId(id);
            user.setName("Test User");
            user.setEmail("test@example.com");
            user.setPasswordHash("newHash");
            user.setRole(UserRoleEnum.CUSTOMER);
            user.setCreatedAt(now);
            user.setUpdatedAt(now);

            assertEquals(id, user.getId());
            assertEquals("Test User", user.getName());
            assertEquals("test@example.com", user.getEmail());
            assertEquals(UserRoleEnum.CUSTOMER, user.getRole());
            assertEquals(now, user.getCreatedAt());
        }
    }
}
