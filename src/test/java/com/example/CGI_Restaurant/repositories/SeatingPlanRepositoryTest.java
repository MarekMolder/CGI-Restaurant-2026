package com.example.CGI_Restaurant.repositories;

/**
 * @author AI (assisted). Used my BookingRepositoryTest + UserRepositoryTest.
 */

import com.example.CGI_Restaurant.config.JpaTestConfig;
import com.example.CGI_Restaurant.domain.entities.Restaurant;
import com.example.CGI_Restaurant.domain.entities.SeatingPlan;
import com.example.CGI_Restaurant.domain.entities.SeatingPlanTypeEnum;
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
class SeatingPlanRepositoryTest {

    @Autowired
    private SeatingPlanRepository repository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    private static Restaurant newRestaurant() {
        var now = java.time.LocalDateTime.now();
        return Restaurant.builder()
                .name("Pärnu Restoran")
                .timezone("Europe/Tallinn")
                .email("info@parnurestoran.ee")
                .phone("+372 5551234")
                .address("Vana-Pärnu 15, Pärnu")
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    private SeatingPlan newSeatingPlan(Restaurant restaurant) {
        return SeatingPlan.builder()
                .name("Peosaal esimene korrus")
                .type(SeatingPlanTypeEnum.FLOOR_1)
                .width(800.0)
                .height(600.0)
                .backgroundSVG(null)
                .active(true)
                .version(1)
                .restaurant(restaurant)
                .build();
    }

    @Nested
    @DisplayName("Save and find")
    class SaveAndFind {

        @Test
        @DisplayName("Saves and finds by id")
        void saveAndFindById() {
            Restaurant restaurant = restaurantRepository.save(newRestaurant());
            SeatingPlan plan = repository.save(newSeatingPlan(restaurant));

            assertNotNull(plan.getId());
            Optional<SeatingPlan> found = repository.findById(plan.getId());
            assertTrue(found.isPresent());
            assertEquals("Peosaal esimene korrus", found.get().getName());
            assertEquals(restaurant.getId(), found.get().getRestaurant().getId());
        }

        @Test
        @DisplayName("FindById returns empty when not found")
        void findByIdReturnsEmptyWhenNotFound() {
            assertTrue(repository.findById(UUID.randomUUID()).isEmpty());
        }
    }
}
