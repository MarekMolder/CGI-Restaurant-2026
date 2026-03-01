package com.example.CGI_Restaurant.domain.entities;

/**
 * @author AI (assisted). Used my BookingTest + UserTest.
 */

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ZoneTest {

    @Nested
    @DisplayName("Builder")
    class BuilderTests {

        @Test
        @DisplayName("Builder creates zone with all attributes")
        void builderCreatesZoneWithAllFields() {
            UUID id = UUID.randomUUID();
            LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
            LocalDateTime updatedAt = LocalDateTime.now();

            Zone zone = Zone.builder()
                    .id(id)
                    .name("Terrace")
                    .type(ZoneTypeEnum.TERRACE)
                    .color("#FF5733")
                    .tableEntities(new ArrayList<>())
                    .features(new HashSet<>())
                    .seatingPlan(null)
                    .createdAt(createdAt)
                    .updatedAt(updatedAt)
                    .build();

            assertNotNull(zone);
            assertEquals(id, zone.getId());
            assertEquals("Terrace", zone.getName());
            assertEquals(ZoneTypeEnum.TERRACE, zone.getType());
            assertEquals("#FF5733", zone.getColor());
            assertNotNull(zone.getTableEntities());
            assertTrue(zone.getTableEntities().isEmpty());
            assertNotNull(zone.getFeatures());
            assertTrue(zone.getFeatures().isEmpty());
            assertEquals(createdAt, zone.getCreatedAt());
            assertEquals(updatedAt, zone.getUpdatedAt());
        }

        @Test
        @DisplayName("Builder without ID (JPA generates it)")
        void builderWithoutId() {
            Zone zone = Zone.builder()
                    .name("Indoor")
                    .type(ZoneTypeEnum.INDOOR)
                    .color("#3366FF")
                    .build();

            assertNull(zone.getId());
            assertEquals("Indoor", zone.getName());
            assertEquals(ZoneTypeEnum.INDOOR, zone.getType());
        }
    }

    @Nested
    @DisplayName("No-args constructor and setters")
    class ConstructorAndSetters {

        @Test
        @DisplayName("No-args constructor creates empty zone")
        void noArgsConstructorCreatesEmptyZone() {
            Zone zone = new Zone();

            assertNotNull(zone);
            assertNull(zone.getId());
            assertNull(zone.getName());
            assertNull(zone.getType());
            assertNotNull(zone.getTableEntities());
            assertTrue(zone.getTableEntities().isEmpty());
            assertNotNull(zone.getFeatures());
            assertTrue(zone.getFeatures().isEmpty());
        }

        @Test
        @DisplayName("Setters and getters work")
        void settersAndGettersWork() {
            Zone zone = new Zone();
            UUID id = UUID.randomUUID();
            LocalDateTime now = LocalDateTime.now();

            zone.setId(id);
            zone.setName("Bar");
            zone.setType(ZoneTypeEnum.BAR);
            zone.setColor("#00FF00");
            zone.setCreatedAt(now);
            zone.setUpdatedAt(now);

            assertEquals(id, zone.getId());
            assertEquals("Bar", zone.getName());
            assertEquals(ZoneTypeEnum.BAR, zone.getType());
            assertEquals(now, zone.getCreatedAt());
        }
    }
}
