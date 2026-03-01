package com.example.CGI_Restaurant.repositories;

/**
 * @author AI (assisted). Used my BookingRepositoryTest + UserRepositoryTest.
 */

import com.example.CGI_Restaurant.config.JpaTestConfig;
import com.example.CGI_Restaurant.domain.entities.MenuItem;
import com.example.CGI_Restaurant.domain.entities.Restaurant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(JpaTestConfig.class)
@ActiveProfiles("test")
class MenuItemRepositoryTest {

    @Autowired
    private MenuItemRepository repository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    private Restaurant saveRestaurant() {
        var now = java.time.LocalDateTime.now();
        return restaurantRepository.save(Restaurant.builder()
                .name("Resto Aed")
                .timezone("Europe/Tallinn")
                .email("info@restoaed.ee")
                .phone("+372 6123456")
                .address("Pärnu mnt 1, Tallinn")
                .createdAt(now).updatedAt(now).build());
    }

    private MenuItem newItem(Restaurant restaurant, String name, String category, String description, BigDecimal price) {
        var now = java.time.LocalDateTime.now();
        return MenuItem.builder()
                .name(name)
                .description(description)
                .priceEur(price)
                .category(category)
                .imageUrl(null)
                .themealdbId(null)
                .restaurant(restaurant)
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
            Restaurant restaurant = saveRestaurant();
            MenuItem item = repository.save(newItem(restaurant, "Caesar salat", "Eelroog", "Roheline salat Caesar kastmega", new BigDecimal("8.50")));
            assertNotNull(item.getId());
            assertTrue(repository.findById(item.getId()).isPresent());
            assertEquals("Caesar salat", item.getName());
            assertEquals("Eelroog", item.getCategory());
        }

        @Test
        @DisplayName("FindByRestaurantIdOrderByCategoryAscNameAsc returns items ordered")
        void findByRestaurantIdOrderByCategoryAscNameAsc() {
            Restaurant restaurant = saveRestaurant();
            repository.save(newItem(restaurant, "Sealihapada", "Praed", "Aeglaselt hautatud sealiha juurviljadega", new BigDecimal("14.90")));
            repository.save(newItem(restaurant, "Šokolaadikook", "Magustoidud", "Must šokolaadi kreem ja vahukoor", new BigDecimal("6.50")));
            repository.save(newItem(restaurant, "Praetine soolane", "Eelroog", "Küpsetatud soolane kreemiga", new BigDecimal("7.00")));

            List<MenuItem> list = repository.findByRestaurantIdOrderByCategoryAscNameAsc(restaurant.getId());
            assertTrue(list.size() >= 3);
            for (int i = 1; i < list.size(); i++) {
                int cat = list.get(i - 1).getCategory().compareTo(list.get(i).getCategory());
                assertTrue(cat <= 0, "Categories should be ascending");
            }
        }

        @Test
        @DisplayName("FindById returns empty when not found")
        void findByIdReturnsEmptyWhenNotFound() {
            assertTrue(repository.findById(UUID.randomUUID()).isEmpty());
        }
    }
}
