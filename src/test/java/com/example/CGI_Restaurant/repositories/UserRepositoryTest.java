package com.example.CGI_Restaurant.repositories;

import com.example.CGI_Restaurant.config.JpaTestConfig;
import com.example.CGI_Restaurant.domain.entities.User;
import com.example.CGI_Restaurant.domain.entities.UserRoleEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(JpaTestConfig.class)
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

    private static User newUser(String fullName, String email, UserRoleEnum role) {
        return User.builder()
                .name(fullName)
                .email(email)
                .passwordHash("$2a$10$hashedPasswordForTestingOnly")
                .role(role)
                .build();
    }

    @Nested
    @DisplayName("Save and find")
    class SaveAndFind {

        @Test
        @DisplayName("Saves and finds by id")
        void saveAndFindById() {
            User user = repository.save(newUser("Gregor Tamm", "gregor.tamm@example.ee", UserRoleEnum.CUSTOMER));
            assertNotNull(user.getId());
            Optional<User> found = repository.findById(user.getId());
            assertTrue(found.isPresent());
            assertEquals("gregor.tamm@example.ee", found.get().getEmail());
            assertEquals("Gregor Tamm", found.get().getName());
        }

        @Test
        @DisplayName("FindByEmail returns user when exists")
        void findByEmailReturnsUserWhenExists() {
            repository.save(newUser("Mari Kask", "mari.kask@restaurant.ee", UserRoleEnum.CUSTOMER));
            Optional<User> found = repository.findByEmail("mari.kask@restaurant.ee");
            assertTrue(found.isPresent());
            assertEquals("mari.kask@restaurant.ee", found.get().getEmail());
            assertEquals("Mari Kask", found.get().getName());
        }

        @Test
        @DisplayName("FindByEmail returns empty when not found")
        void findByEmailReturnsEmptyWhenNotFound() {
            Optional<User> found = repository.findByEmail("olematu.kasutaja@example.ee");
            assertTrue(found.isEmpty());
        }

        @Test
        @DisplayName("FindById returns empty when not found")
        void findByIdReturnsEmptyWhenNotFound() {
            assertTrue(repository.findById(UUID.randomUUID()).isEmpty());
        }
    }
}
