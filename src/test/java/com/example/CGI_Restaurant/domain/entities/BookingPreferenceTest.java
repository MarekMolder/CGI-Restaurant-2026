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

class BookingPreferenceTest {

    @Nested
    @DisplayName("Builder")
    class BuilderTests {

        @Test
        @DisplayName("Builder creates booking preference with all attributes")
        void builderCreatesBookingPreferenceWithAllFields() {
            UUID id = UUID.randomUUID();
            Booking booking = new Booking();
            booking.setId(UUID.randomUUID());
            Feature feature = new Feature();
            feature.setId(UUID.randomUUID());
            LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
            LocalDateTime updatedAt = LocalDateTime.now();

            BookingPreference pref = BookingPreference.builder()
                    .id(id)
                    .priority(PreferencePriorityEnum.HIGH)
                    .booking(booking)
                    .feature(feature)
                    .createdAt(createdAt)
                    .updatedAt(updatedAt)
                    .build();

            assertNotNull(pref);
            assertEquals(id, pref.getId());
            assertEquals(PreferencePriorityEnum.HIGH, pref.getPriority());
            assertEquals(booking, pref.getBooking());
            assertEquals(feature, pref.getFeature());
            assertEquals(createdAt, pref.getCreatedAt());
            assertEquals(updatedAt, pref.getUpdatedAt());
        }

        @Test
        @DisplayName("Builder without ID (JPA generates it)")
        void builderWithoutId() {
            Booking booking = new Booking();
            Feature feature = new Feature();
            BookingPreference pref = BookingPreference.builder()
                    .priority(PreferencePriorityEnum.MEDIUM)
                    .booking(booking)
                    .feature(feature)
                    .build();

            assertNull(pref.getId());
            assertEquals(PreferencePriorityEnum.MEDIUM, pref.getPriority());
        }
    }

    @Nested
    @DisplayName("No-args constructor and setters")
    class ConstructorAndSetters {

        @Test
        @DisplayName("No-args constructor creates empty booking preference")
        void noArgsConstructorCreatesEmptyBookingPreference() {
            BookingPreference pref = new BookingPreference();

            assertNotNull(pref);
            assertNull(pref.getId());
            assertNull(pref.getPriority());
            assertNull(pref.getBooking());
            assertNull(pref.getFeature());
        }

        @Test
        @DisplayName("Setters and getters work")
        void settersAndGettersWork() {
            BookingPreference pref = new BookingPreference();
            UUID id = UUID.randomUUID();
            Booking booking = new Booking();
            Feature feature = new Feature();
            LocalDateTime now = LocalDateTime.now();

            pref.setId(id);
            pref.setPriority(PreferencePriorityEnum.LOW);
            pref.setBooking(booking);
            pref.setFeature(feature);
            pref.setCreatedAt(now);
            pref.setUpdatedAt(now);

            assertEquals(id, pref.getId());
            assertEquals(PreferencePriorityEnum.LOW, pref.getPriority());
            assertEquals(booking, pref.getBooking());
            assertEquals(feature, pref.getFeature());
            assertEquals(now, pref.getCreatedAt());
        }
    }
}
