package com.example.CGI_Restaurant.domain.entities;

/**
 * @author AI (assisted). Used my BookingTest + UserTest.
 */

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantTest {

    @Nested
    @DisplayName("Builder")
    class BuilderTests {

        @Test
        @DisplayName("Builder creates restaurant with all attributes")
        void builderCreatesRestaurantWithAllFields() {
            UUID id = UUID.randomUUID();
            LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
            LocalDateTime updatedAt = LocalDateTime.now();

            Restaurant restaurant = Restaurant.builder()
                    .id(id)
                    .name("Test Resto")
                    .timezone("Europe/Tallinn")
                    .email("resto@test.ee")
                    .phone("+372 1234567")
                    .address("Test 1, Tallinn")
                    .seatingPlans(new ArrayList<>())
                    .menuItems(new ArrayList<>())
                    .createdAt(createdAt)
                    .updatedAt(updatedAt)
                    .build();

            assertNotNull(restaurant);
            assertEquals(id, restaurant.getId());
            assertEquals("Test Resto", restaurant.getName());
            assertEquals("Europe/Tallinn", restaurant.getTimezone());
            assertEquals("resto@test.ee", restaurant.getEmail());
            assertEquals("+372 1234567", restaurant.getPhone());
            assertEquals("Test 1, Tallinn", restaurant.getAddress());
            assertNotNull(restaurant.getSeatingPlans());
            assertTrue(restaurant.getSeatingPlans().isEmpty());
            assertNotNull(restaurant.getMenuItems());
            assertTrue(restaurant.getMenuItems().isEmpty());
            assertEquals(createdAt, restaurant.getCreatedAt());
            assertEquals(updatedAt, restaurant.getUpdatedAt());
        }

        @Test
        @DisplayName("Builder without ID (JPA generates it)")
        void builderWithoutId() {
            Restaurant restaurant = Restaurant.builder()
                    .name("No Id Resto")
                    .timezone("UTC")
                    .email("a@b.ee")
                    .phone("123")
                    .address("Addr")
                    .build();

            assertNull(restaurant.getId());
            assertEquals("No Id Resto", restaurant.getName());
        }
    }

    @Nested
    @DisplayName("No-args constructor and setters")
    class ConstructorAndSetters {

        @Test
        @DisplayName("no-args constructor creates empty restaurant")
        void noArgsConstructorCreatesEmptyRestaurant() {
            Restaurant restaurant = new Restaurant();

            assertNotNull(restaurant);
            assertNull(restaurant.getId());
            assertNull(restaurant.getName());

            assertNotNull(restaurant.getSeatingPlans());
            assertTrue(restaurant.getSeatingPlans().isEmpty());
            assertNotNull(restaurant.getMenuItems());
            assertTrue(restaurant.getMenuItems().isEmpty());
        }

        @Test
        @DisplayName("Setters and getters work")
        void settersAndGettersWork() {
            Restaurant restaurant = new Restaurant();
            UUID id = UUID.randomUUID();
            LocalDateTime now = LocalDateTime.now();

            restaurant.setId(id);
            restaurant.setName("Setter Resto");
            restaurant.setTimezone("Europe/Tallinn");
            restaurant.setEmail("set@test.ee");
            restaurant.setPhone("555");
            restaurant.setAddress("Street 1");
            restaurant.setSeatingPlans(new ArrayList<>());
            restaurant.setMenuItems(new ArrayList<>());
            restaurant.setCreatedAt(now);
            restaurant.setUpdatedAt(now);

            assertEquals(id, restaurant.getId());
            assertEquals("Setter Resto", restaurant.getName());
            assertEquals(now, restaurant.getCreatedAt());
            assertEquals(now, restaurant.getUpdatedAt());
        }
    }
}
