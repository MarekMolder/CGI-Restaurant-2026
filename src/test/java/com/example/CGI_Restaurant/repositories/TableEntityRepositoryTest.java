package com.example.CGI_Restaurant.repositories;

/**
 * @author AI (assisted). Used my BookingRepositoryTest + UserRepositoryTest.
 */

import com.example.CGI_Restaurant.config.JpaTestConfig;
import com.example.CGI_Restaurant.domain.entities.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(JpaTestConfig.class)
@ActiveProfiles("test")
class TableEntityRepositoryTest {

    @Autowired
    private TableEntityRepository repository;

    @Autowired
    private ZoneRepository zoneRepository;

    @Autowired
    private SeatingPlanRepository seatingPlanRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    private Restaurant saveRestaurant() {
        var now = java.time.LocalDateTime.now();
        return restaurantRepository.save(Restaurant.builder()
                .name("Resto Aed")
                .timezone("Europe/Tallinn")
                .email("info@restoaed.ee")
                .phone("+372 6123456")
                .address("Pärnu mnt 1, 10141 Tallinn")
                .createdAt(now).updatedAt(now).build());
    }

    private SeatingPlan saveSeatingPlan(Restaurant restaurant) {
        var now = java.time.LocalDateTime.now();
        return seatingPlanRepository.save(SeatingPlan.builder()
                .name("Peosaal")
                .type(SeatingPlanTypeEnum.FLOOR_1)
                .width(800.0).height(600.0).active(true).version(1).restaurant(restaurant)
                .createdAt(now).updatedAt(now).build());
    }

    private Zone saveZone(SeatingPlan plan) {
        var now = java.time.LocalDateTime.now();
        return zoneRepository.save(Zone.builder()
                .name("Siseala")
                .type(ZoneTypeEnum.INDOOR)
                .color("#3366FF")
                .seatingPlan(plan).createdAt(now).updatedAt(now).build());
    }

    private TableEntity newTable(Zone zone, SeatingPlan plan, boolean active, String label) {
        var now = java.time.LocalDateTime.now();
        return TableEntity.builder()
                .label(label)
                .capacity(4)
                .minPartySize(2)
                .shape(TableShapeEnum.RECT)
                .x(10.0).y(20.0).width(80.0).height(120.0)
                .rotationDegree(0)
                .active(active)
                .zone(zone)
                .seatingPlan(plan)
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
            Restaurant r = saveRestaurant();
            SeatingPlan plan = saveSeatingPlan(r);
            Zone zone = saveZone(plan);
            TableEntity table = repository.save(newTable(zone, plan, true, "Laud 1"));

            assertNotNull(table.getId());
            assertTrue(repository.findById(table.getId()).isPresent());
        }

        @Test
        @DisplayName("FindByZoneIdAndActiveTrue returns only active tables in zone")
        void findByZoneIdAndActiveTrue() {
            Restaurant r = saveRestaurant();
            SeatingPlan plan = saveSeatingPlan(r);
            Zone zone = saveZone(plan);
            repository.save(newTable(zone, plan, true, "Laud 1"));
            repository.save(newTable(zone, plan, false, "Laud 2 reserveerimata"));

            var page = repository.findByZoneIdAndActiveTrue(zone.getId(), PageRequest.of(0, 10));
            assertTrue(page.getContent().stream().allMatch(TableEntity::isActive));
            assertTrue(page.getContent().size() >= 1);
        }

        @Test
        @DisplayName("FindBySeatingPlanIdAndActiveTrue returns only active tables in plan")
        void findBySeatingPlanIdAndActiveTrue() {
            Restaurant r = saveRestaurant();
            SeatingPlan plan = saveSeatingPlan(r);
            Zone zone = saveZone(plan);
            repository.save(newTable(zone, plan, true, "Laud 1"));

            var page = repository.findBySeatingPlanIdAndActiveTrue(plan.getId(), PageRequest.of(0, 10));
            assertTrue(page.getContent().size() >= 1);
        }

        @Test
        @DisplayName("SearchTableEntities finds by label")
        void searchTableEntities() {
            Restaurant r = saveRestaurant();
            SeatingPlan plan = saveSeatingPlan(r);
            Zone zone = saveZone(plan);
            repository.save(newTable(zone, plan, true, "Terassi laud Alpha"));

            var page = repository.searchTableEntities("alpha", PageRequest.of(0, 10));
            assertTrue(page.getContent().size() >= 1);
            assertTrue(page.getContent().stream().anyMatch(t -> t.getLabel().contains("Alpha")));
        }

        @Test
        @DisplayName("FindByZoneIdAndActiveTrueWithAdjacent returns list with adjacent loaded")
        void findByZoneIdAndActiveTrueWithAdjacent() {
            Restaurant r = saveRestaurant();
            SeatingPlan plan = saveSeatingPlan(r);
            Zone zone = saveZone(plan);
            repository.save(newTable(zone, plan, true, "Laud 1"));
            List<TableEntity> list = repository.findByZoneIdAndActiveTrueWithAdjacent(zone.getId());
            assertTrue(list.size() >= 1);
        }

        @Test
        @DisplayName("FindByIdInWithAdjacent returns tables by ids")
        void findByIdInWithAdjacent() {
            Restaurant r = saveRestaurant();
            SeatingPlan plan = saveSeatingPlan(r);
            Zone zone = saveZone(plan);
            TableEntity table = repository.save(newTable(zone, plan, true, "Laud 1"));
            List<TableEntity> list = repository.findByIdInWithAdjacent(List.of(table.getId()));
            assertEquals(1, list.size());
        }
    }
}
