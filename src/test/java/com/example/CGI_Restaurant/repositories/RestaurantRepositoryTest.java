package com.example.CGI_Restaurant.repositories;

/**
 * @author AI (assisted). Used my BookingRepositoryTest + UserRepositoryTest.
 */

import com.example.CGI_Restaurant.config.JpaTestConfig;
import com.example.CGI_Restaurant.domain.entities.Restaurant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(JpaTestConfig.class)
@ActiveProfiles("test")
class RestaurantRepositoryTest {

    @Autowired
    private RestaurantRepository repository;

    private static Restaurant newRestaurant(String name) {
        LocalDateTime now = LocalDateTime.now();
        return Restaurant.builder()
                .name(name)
                .timezone("Europe/Tallinn")
                .email("info@restoran.ee")
                .phone("+372 6123456")
                .address("Pärnu mnt 1, 10141 Tallinn")
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    @Nested
    @DisplayName("Save and find")
    class SaveAndFind {

        @Test
        @DisplayName("Saves and finds by id")
        void saveAndFindById() {
            Restaurant restaurant = newRestaurant("Resto Aed");
            Restaurant saved = repository.save(restaurant);

            assertNotNull(saved.getId());
            Optional<Restaurant> found = repository.findById(saved.getId());
            assertTrue(found.isPresent());
            assertEquals("Resto Aed", found.get().getName());
        }

        @Test
        @DisplayName("FindById returns empty when not found")
        void findByIdReturnsEmptyWhenNotFound() {
            Optional<Restaurant> found = repository.findById(UUID.randomUUID());
            assertTrue(found.isEmpty());
        }

        @Test
        @DisplayName("FindAll returns all saved restaurants")
        void findAllReturnsAll() {
            repository.save(newRestaurant("Resto Aed"));
            repository.save(newRestaurant("Vana Kelder"));

            List<Restaurant> all = repository.findAll();
            assertTrue(all.size() >= 2);
        }
    }

    @Nested
    @DisplayName("Delete")
    class Delete {

        @Test
        @DisplayName("Deletes by entity")
        void deleteRemovesEntity() {
            Restaurant saved = repository.save(newRestaurant("Broneeringu kustutatav restoran"));
            UUID id = saved.getId();

            repository.delete(saved);
            assertTrue(repository.findById(id).isEmpty());
        }
    }
}
