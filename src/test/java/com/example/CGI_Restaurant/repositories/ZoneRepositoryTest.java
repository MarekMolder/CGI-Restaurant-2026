package com.example.CGI_Restaurant.repositories;

/**
 * @author AI (assisted). Used my BookingRepositoryTest + UserRepositoryTest.
 */

import com.example.CGI_Restaurant.config.JpaTestConfig;
import com.example.CGI_Restaurant.domain.entities.Feature;
import com.example.CGI_Restaurant.domain.entities.FeatureCodeEnum;
import com.example.CGI_Restaurant.domain.entities.Restaurant;
import com.example.CGI_Restaurant.domain.entities.SeatingPlan;
import com.example.CGI_Restaurant.domain.entities.SeatingPlanTypeEnum;
import com.example.CGI_Restaurant.domain.entities.Zone;
import com.example.CGI_Restaurant.domain.entities.ZoneTypeEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(JpaTestConfig.class)
@ActiveProfiles("test")
class ZoneRepositoryTest {

    @Autowired
    private ZoneRepository repository;

    @Autowired
    private SeatingPlanRepository seatingPlanRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private FeatureRepository featureRepository;

    private Zone newZone(SeatingPlan plan, Set<Feature> features) {
        return Zone.builder()
                .name("Terrass")
                .type(ZoneTypeEnum.TERRACE)
                .color("#2E7D32")
                .seatingPlan(plan)
                .features(features != null ? features : Set.of())
                .build();
    }

    @Nested
    @DisplayName("Save and find")
    class SaveAndFind {

        @Test
        @DisplayName("Saves and finds by id")
        void saveAndFindById() {
            var now = java.time.LocalDateTime.now();
            Restaurant restaurant = restaurantRepository.save(Restaurant.builder()
                    .name("Vana Kelder")
                    .timezone("Europe/Tallinn")
                    .email("info@vanakelder.ee")
                    .phone("+372 6123456")
                    .address("Vana-Viru 5, Tallinn")
                    .createdAt(now).updatedAt(now).build());
            SeatingPlan plan = seatingPlanRepository.save(SeatingPlan.builder()
                    .name("Siseala plaan")
                    .type(SeatingPlanTypeEnum.FLOOR_1)
                    .width(400.0).height(300.0)
                    .active(true).version(1).restaurant(restaurant)
                    .createdAt(now).updatedAt(now).build());

            Zone zone = repository.save(newZone(plan, Set.of()));
            assertNotNull(zone.getId());
            assertEquals("Terrass", zone.getName());
            assertEquals(ZoneTypeEnum.TERRACE, zone.getType());
        }

        @Test
        @DisplayName("FindByIdInWithFeatures returns zones with features loaded")
        void findByIdInWithFeatures() {
            var now = java.time.LocalDateTime.now();
            Restaurant restaurant = restaurantRepository.save(Restaurant.builder()
                    .name("Resto Aed")
                    .timezone("Europe/Tallinn")
                    .email("info@restoaed.ee")
                    .phone("+372 5559876")
                    .address("Narva mnt 2, Tallinn")
                    .createdAt(now).updatedAt(now).build());
            SeatingPlan plan = seatingPlanRepository.save(SeatingPlan.builder()
                    .name("Baari tsoon")
                    .type(SeatingPlanTypeEnum.FLOOR_1)
                    .width(200.0).height(150.0).active(true).version(1).restaurant(restaurant)
                    .createdAt(now).updatedAt(now).build());
            Feature feature = featureRepository.save(Feature.builder()
                    .code(FeatureCodeEnum.WINDOW)
                    .name("Akna vaade")
                    .build());

            Zone zone = repository.save(newZone(plan, Set.of(feature)));
            List<Zone> withFeatures = repository.findByIdInWithFeatures(List.of(zone.getId()));
            assertEquals(1, withFeatures.size());
            assertNotNull(withFeatures.get(0).getFeatures());
            assertTrue(withFeatures.get(0).getFeatures().size() >= 1);
        }
    }
}
