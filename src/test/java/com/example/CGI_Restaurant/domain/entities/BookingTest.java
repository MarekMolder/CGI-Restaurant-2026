package com.example.CGI_Restaurant.domain.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BookingTest {

    @Nested
    @DisplayName("Builder")
    class BuilderTests {

        @Test
        @DisplayName("Builder creates booking with all attributes")
        void builderCreatesBookingWithAllFields() {
            UUID id = UUID.randomUUID();
            LocalDateTime startAt = LocalDateTime.now().plusDays(1);
            LocalDateTime endAt = startAt.plusHours(2);
            LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
            LocalDateTime updatedAt = LocalDateTime.now();

            Booking booking = Booking.builder()
                    .id(id)
                    .guestName("John Doe")
                    .guestEmail("john@example.com")
                    .startAt(startAt)
                    .endAt(endAt)
                    .partySize(4)
                    .status(BookingStatusEnum.CONFIRMED)
                    .qrToken("qr-token-123")
                    .qrCodes(new ArrayList<>())
                    .specialRequests("Window seat please")
                    .user(null)
                    .bookingPreferences(new ArrayList<>())
                    .bookingTables(new ArrayList<>())
                    .createdAt(createdAt)
                    .updatedAt(updatedAt)
                    .build();

            assertNotNull(booking);
            assertEquals(id, booking.getId());
            assertEquals("John Doe", booking.getGuestName());
            assertEquals("john@example.com", booking.getGuestEmail());
            assertEquals(startAt, booking.getStartAt());
            assertEquals(endAt, booking.getEndAt());
            assertEquals(4, booking.getPartySize());
            assertEquals(BookingStatusEnum.CONFIRMED, booking.getStatus());
            assertEquals("qr-token-123", booking.getQrToken());
            assertNotNull(booking.getQrCodes());
            assertTrue(booking.getQrCodes().isEmpty());
            assertEquals("Window seat please", booking.getSpecialRequests());
            assertNotNull(booking.getBookingPreferences());
            assertNotNull(booking.getBookingTables());
            assertEquals(createdAt, booking.getCreatedAt());
            assertEquals(updatedAt, booking.getUpdatedAt());
        }

        @Test
        @DisplayName("Builder without ID (JPA generates it)")
        void builderWithoutId() {
            LocalDateTime start = LocalDateTime.now();
            Booking booking = Booking.builder()
                    .guestName("Jane")
                    .guestEmail("jane@test.ee")
                    .startAt(start)
                    .endAt(start.plusHours(1))
                    .partySize(2)
                    .status(BookingStatusEnum.PENDING)
                    .build();

            assertNull(booking.getId());
            assertEquals("Jane", booking.getGuestName());
            assertEquals(BookingStatusEnum.PENDING, booking.getStatus());
        }
    }

    @Nested
    @DisplayName("No-args constructor and setters")
    class ConstructorAndSetters {

        @Test
        @DisplayName("No-args constructor creates empty booking")
        void noArgsConstructorCreatesEmptyBooking() {
            Booking booking = new Booking();

            assertNotNull(booking);
            assertNull(booking.getId());
            assertNull(booking.getGuestName());
            assertNull(booking.getStatus());
            assertNotNull(booking.getQrCodes());
            assertTrue(booking.getQrCodes().isEmpty());
            assertNotNull(booking.getBookingPreferences());
            assertNotNull(booking.getBookingTables());
        }

        @Test
        @DisplayName("Setters and getters work")
        void settersAndGettersWork() {
            Booking booking = new Booking();
            UUID id = UUID.randomUUID();
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime start = now.plusDays(1);
            LocalDateTime end = start.plusHours(2);

            booking.setId(id);
            booking.setGuestName("Guest");
            booking.setGuestEmail("guest@mail.com");
            booking.setStartAt(start);
            booking.setEndAt(end);
            booking.setPartySize(6);
            booking.setStatus(BookingStatusEnum.COMPLETED);
            booking.setQrToken("token");
            booking.setSpecialRequests("None");
            booking.setCreatedAt(now);
            booking.setUpdatedAt(now);

            assertEquals(id, booking.getId());
            assertEquals("Guest", booking.getGuestName());
            assertEquals(6, booking.getPartySize());
            assertEquals(BookingStatusEnum.COMPLETED, booking.getStatus());
            assertEquals(now, booking.getCreatedAt());
        }
    }
}
