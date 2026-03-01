package com.example.CGI_Restaurant.domain.entities;

/**
 * @author AI (assisted). Used my BookingTest + UserTest.
 */

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BookingTableTest {

    @Nested
    @DisplayName("Builder")
    class BuilderTests {

        @Test
        @DisplayName("Builder creates booking table with all attributes")
        void builderCreatesBookingTableWithAllFields() {
            UUID id = UUID.randomUUID();
            Booking booking = new Booking();
            booking.setId(UUID.randomUUID());
            TableEntity tableEntity = new TableEntity();
            tableEntity.setId(UUID.randomUUID());
            LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
            LocalDateTime updatedAt = LocalDateTime.now();

            BookingTable bookingTable = BookingTable.builder()
                    .id(id)
                    .booking(booking)
                    .tableEntity(tableEntity)
                    .createdAt(createdAt)
                    .updatedAt(updatedAt)
                    .build();

            assertNotNull(bookingTable);
            assertEquals(id, bookingTable.getId());
            assertEquals(booking, bookingTable.getBooking());
            assertEquals(tableEntity, bookingTable.getTableEntity());
            assertEquals(createdAt, bookingTable.getCreatedAt());
            assertEquals(updatedAt, bookingTable.getUpdatedAt());
        }

        @Test
        @DisplayName("Builder without ID (JPA generates it)")
        void builderWithoutId() {
            Booking booking = new Booking();
            TableEntity tableEntity = new TableEntity();
            BookingTable bookingTable = BookingTable.builder()
                    .booking(booking)
                    .tableEntity(tableEntity)
                    .build();

            assertNull(bookingTable.getId());
            assertNotNull(bookingTable.getBooking());
            assertNotNull(bookingTable.getTableEntity());
        }
    }

    @Nested
    @DisplayName("No-args constructor and setters")
    class ConstructorAndSetters {

        @Test
        @DisplayName("No-args constructor creates empty booking table")
        void noArgsConstructorCreatesEmptyBookingTable() {
            BookingTable bookingTable = new BookingTable();

            assertNotNull(bookingTable);
            assertNull(bookingTable.getId());
            assertNull(bookingTable.getBooking());
            assertNull(bookingTable.getTableEntity());
        }

        @Test
        @DisplayName("Setters and getters work")
        void settersAndGettersWork() {
            BookingTable bookingTable = new BookingTable();
            UUID id = UUID.randomUUID();
            Booking booking = new Booking();
            TableEntity tableEntity = new TableEntity();
            LocalDateTime now = LocalDateTime.now();

            bookingTable.setId(id);
            bookingTable.setBooking(booking);
            bookingTable.setTableEntity(tableEntity);
            bookingTable.setCreatedAt(now);
            bookingTable.setUpdatedAt(now);

            assertEquals(id, bookingTable.getId());
            assertEquals(booking, bookingTable.getBooking());
            assertEquals(tableEntity, bookingTable.getTableEntity());
            assertEquals(now, bookingTable.getCreatedAt());
        }
    }
}
