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

class TableEntityTest {

    @Nested
    @DisplayName("Builder")
    class BuilderTests {

        @Test
        @DisplayName("Builder creates table entity with all attributes")
        void builderCreatesTableEntityWithAllFields() {
            UUID id = UUID.randomUUID();
            LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
            LocalDateTime updatedAt = LocalDateTime.now();

            TableEntity table = TableEntity.builder()
                    .id(id)
                    .label("T1")
                    .capacity(4)
                    .minPartySize(2)
                    .shape(TableShapeEnum.RECT)
                    .x(10.0)
                    .y(20.0)
                    .width(80.0)
                    .height(120.0)
                    .rotationDegree(0)
                    .active(true)
                    .bookingTables(new ArrayList<>())
                    .zone(null)
                    .seatingPlan(null)
                    .adjacentTables(new HashSet<>())
                    .createdAt(createdAt)
                    .updatedAt(updatedAt)
                    .build();

            assertNotNull(table);
            assertEquals(id, table.getId());
            assertEquals("T1", table.getLabel());
            assertEquals(4, table.getCapacity());
            assertEquals(2, table.getMinPartySize());
            assertEquals(TableShapeEnum.RECT, table.getShape());
            assertEquals(10.0, table.getX());
            assertEquals(20.0, table.getY());
            assertEquals(80.0, table.getWidth());
            assertEquals(120.0, table.getHeight());
            assertEquals(0, table.getRotationDegree());
            assertTrue(table.isActive());
            assertNotNull(table.getBookingTables());
            assertTrue(table.getBookingTables().isEmpty());
            assertNotNull(table.getAdjacentTables());
            assertTrue(table.getAdjacentTables().isEmpty());
            assertEquals(createdAt, table.getCreatedAt());
            assertEquals(updatedAt, table.getUpdatedAt());
        }

        @Test
        @DisplayName("Builder without ID (JPA generates it)")
        void builderWithoutId() {
            TableEntity table = TableEntity.builder()
                    .label("T2")
                    .capacity(2)
                    .minPartySize(1)
                    .shape(TableShapeEnum.CIRCLE)
                    .x(0)
                    .y(0)
                    .width(60)
                    .height(60)
                    .rotationDegree(90)
                    .active(false)
                    .build();

            assertNull(table.getId());
            assertEquals("T2", table.getLabel());
            assertEquals(TableShapeEnum.CIRCLE, table.getShape());
        }
    }

    @Nested
    @DisplayName("No-args constructor and setters")
    class ConstructorAndSetters {

        @Test
        @DisplayName("No-args constructor creates empty table entity")
        void noArgsConstructorCreatesEmptyTableEntity() {
            TableEntity table = new TableEntity();

            assertNotNull(table);
            assertNull(table.getId());
            assertNull(table.getLabel());
            assertNull(table.getShape());
            assertNotNull(table.getBookingTables());
            assertTrue(table.getBookingTables().isEmpty());
            assertNotNull(table.getAdjacentTables());
            assertTrue(table.getAdjacentTables().isEmpty());
        }

        @Test
        @DisplayName("Setters and getters work")
        void settersAndGettersWork() {
            TableEntity table = new TableEntity();
            UUID id = UUID.randomUUID();
            LocalDateTime now = LocalDateTime.now();

            table.setId(id);
            table.setLabel("T3");
            table.setCapacity(6);
            table.setMinPartySize(4);
            table.setShape(TableShapeEnum.OVAL);
            table.setX(5.5);
            table.setY(10.5);
            table.setWidth(100);
            table.setHeight(80);
            table.setRotationDegree(45);
            table.setActive(true);
            table.setCreatedAt(now);
            table.setUpdatedAt(now);

            assertEquals(id, table.getId());
            assertEquals("T3", table.getLabel());
            assertEquals(6, table.getCapacity());
            assertEquals(TableShapeEnum.OVAL, table.getShape());
            assertEquals(45, table.getRotationDegree());
            assertEquals(now, table.getCreatedAt());
        }
    }
}
