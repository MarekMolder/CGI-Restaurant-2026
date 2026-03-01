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

class SeatingPlanTest {

    @Nested
    @DisplayName("Builder")
    class BuilderTests {

        @Test
        @DisplayName("Builder creates seating plan with all attributes")
        void builderCreatesSeatingPlanWithAllFields() {
            UUID id = UUID.randomUUID();
            LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
            LocalDateTime updatedAt = LocalDateTime.now();

            SeatingPlan plan = SeatingPlan.builder()
                    .id(id)
                    .name("Main Hall")
                    .type(SeatingPlanTypeEnum.FLOOR_1)
                    .width(800.0)
                    .height(600.0)
                    .backgroundSVG("<svg></svg>")
                    .active(true)
                    .version(1)
                    .tableEntities(new ArrayList<>())
                    .zones(new ArrayList<>())
                    .restaurant(null)
                    .createdAt(createdAt)
                    .updatedAt(updatedAt)
                    .build();

            assertNotNull(plan);
            assertEquals(id, plan.getId());
            assertEquals("Main Hall", plan.getName());
            assertEquals(SeatingPlanTypeEnum.FLOOR_1, plan.getType());
            assertEquals(800.0, plan.getWidth());
            assertEquals(600.0, plan.getHeight());
            assertEquals("<svg></svg>", plan.getBackgroundSVG());
            assertTrue(plan.isActive());
            assertEquals(1, plan.getVersion());
            assertNotNull(plan.getTableEntities());
            assertTrue(plan.getTableEntities().isEmpty());
            assertNotNull(plan.getZones());
            assertTrue(plan.getZones().isEmpty());
            assertEquals(createdAt, plan.getCreatedAt());
            assertEquals(updatedAt, plan.getUpdatedAt());
        }

        @Test
        @DisplayName("Builder without ID (JPA generates it)")
        void builderWithoutId() {
            SeatingPlan plan = SeatingPlan.builder()
                    .name("Terrace Plan")
                    .type(SeatingPlanTypeEnum.OUTDOOR)
                    .width(400)
                    .height(300)
                    .active(false)
                    .version(0)
                    .build();

            assertNull(plan.getId());
            assertEquals("Terrace Plan", plan.getName());
            assertEquals(SeatingPlanTypeEnum.OUTDOOR, plan.getType());
        }
    }

    @Nested
    @DisplayName("No-args constructor and setters")
    class ConstructorAndSetters {

        @Test
        @DisplayName("No-args constructor creates empty seating plan")
        void noArgsConstructorCreatesEmptySeatingPlan() {
            SeatingPlan plan = new SeatingPlan();

            assertNotNull(plan);
            assertNull(plan.getId());
            assertNull(plan.getName());
            assertNull(plan.getType());
            assertNotNull(plan.getTableEntities());
            assertTrue(plan.getTableEntities().isEmpty());
            assertNotNull(plan.getZones());
            assertTrue(plan.getZones().isEmpty());
        }

        @Test
        @DisplayName("Setters and getters work")
        void settersAndGettersWork() {
            SeatingPlan plan = new SeatingPlan();
            UUID id = UUID.randomUUID();
            LocalDateTime now = LocalDateTime.now();

            plan.setId(id);
            plan.setName("Private Area");
            plan.setType(SeatingPlanTypeEnum.PRIVATE_AREA);
            plan.setWidth(200);
            plan.setHeight(150);
            plan.setBackgroundSVG(null);
            plan.setActive(true);
            plan.setVersion(2);
            plan.setCreatedAt(now);
            plan.setUpdatedAt(now);

            assertEquals(id, plan.getId());
            assertEquals("Private Area", plan.getName());
            assertEquals(SeatingPlanTypeEnum.PRIVATE_AREA, plan.getType());
            assertEquals(2, plan.getVersion());
            assertEquals(now, plan.getCreatedAt());
        }
    }
}
