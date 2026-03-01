package com.example.CGI_Restaurant.domain.entities;

/**
 * @author AI (assisted). Used my BookingTest + UserTest.
 */

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MenuItemTest {

    @Nested
    @DisplayName("Builder")
    class BuilderTests {

        @Test
        @DisplayName("Builder creates menu item with all attributes")
        void builderCreatesMenuItemWithAllFields() {
            UUID id = UUID.randomUUID();
            LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
            LocalDateTime updatedAt = LocalDateTime.now();

            MenuItem item = MenuItem.builder()
                    .id(id)
                    .name("Caesar Salad")
                    .description("Fresh greens with Caesar dressing")
                    .priceEur(new BigDecimal("12.50"))
                    .category("Salads")
                    .imageUrl("https://example.com/salad.jpg")
                    .themealdbId("52772")
                    .restaurant(null)
                    .createdAt(createdAt)
                    .updatedAt(updatedAt)
                    .build();

            assertNotNull(item);
            assertEquals(id, item.getId());
            assertEquals("Caesar Salad", item.getName());
            assertEquals("Fresh greens with Caesar dressing", item.getDescription());
            assertEquals(new BigDecimal("12.50"), item.getPriceEur());
            assertEquals("Salads", item.getCategory());
            assertEquals("https://example.com/salad.jpg", item.getImageUrl());
            assertEquals("52772", item.getThemealdbId());
            assertEquals(createdAt, item.getCreatedAt());
            assertEquals(updatedAt, item.getUpdatedAt());
        }

        @Test
        @DisplayName("Builder without ID (JPA generates it)")
        void builderWithoutId() {
            MenuItem item = MenuItem.builder()
                    .name("Coffee")
                    .priceEur(new BigDecimal("3.00"))
                    .category("Drinks")
                    .build();

            assertNull(item.getId());
            assertEquals("Coffee", item.getName());
            assertEquals(new BigDecimal("3.00"), item.getPriceEur());
        }
    }

    @Nested
    @DisplayName("No-args constructor and setters")
    class ConstructorAndSetters {

        @Test
        @DisplayName("No-args constructor creates empty menu item")
        void noArgsConstructorCreatesEmptyMenuItem() {
            MenuItem item = new MenuItem();

            assertNotNull(item);
            assertNull(item.getId());
            assertNull(item.getName());
            assertNull(item.getPriceEur());
            assertNull(item.getRestaurant());
        }

        @Test
        @DisplayName("Setters and getters work")
        void settersAndGettersWork() {
            MenuItem item = new MenuItem();
            UUID id = UUID.randomUUID();
            LocalDateTime now = LocalDateTime.now();

            item.setId(id);
            item.setName("Tea");
            item.setDescription("Green tea");
            item.setPriceEur(new BigDecimal("2.50"));
            item.setCategory("Drinks");
            item.setImageUrl("http://tea.jpg");
            item.setThemealdbId(null);
            item.setCreatedAt(now);
            item.setUpdatedAt(now);

            assertEquals(id, item.getId());
            assertEquals("Tea", item.getName());
            assertEquals(new BigDecimal("2.50"), item.getPriceEur());
            assertEquals(now, item.getCreatedAt());
        }
    }
}
