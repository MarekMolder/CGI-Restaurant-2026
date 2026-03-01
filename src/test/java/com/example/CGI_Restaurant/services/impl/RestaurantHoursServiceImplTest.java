package com.example.CGI_Restaurant.services.impl;

/**
 * @author AI (assisted). Used my BookingSeviceImplTest.
 */

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RestaurantHoursServiceImplTest {

    private RestaurantHoursServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new RestaurantHoursServiceImpl();
        ReflectionTestUtils.setField(service, "weekdayOpenStr", "10:00");
        ReflectionTestUtils.setField(service, "weekdayCloseStr", "18:00");
        ReflectionTestUtils.setField(service, "weekendOpenStr", "10:00");
        ReflectionTestUtils.setField(service, "weekendCloseStr", "22:00");
        ReflectionTestUtils.setField(service, "bookingDurationHours", 2);
    }

    @Nested
    @DisplayName("getBookingDurationHours")
    class GetBookingDurationHours {

        @Test
        @DisplayName("returns configured duration")
        void returnsConfiguredDuration() {
            assertEquals(2, service.getBookingDurationHours());
        }
    }

    @Nested
    @DisplayName("isWithinOpeningHours")
    class IsWithinOpeningHours {

        @Test
        @DisplayName("returns true when weekday slot fully within 10:00–18:00")
        void weekdayWithinHours() {
            LocalDateTime start = LocalDateTime.of(2025, 3, 4, 12, 0); // Tuesday 12:00
            LocalDateTime end = LocalDateTime.of(2025, 3, 4, 14, 0);   // Tuesday 14:00
            assertTrue(service.isWithinOpeningHours(start, end));
        }

        @Test
        @DisplayName("returns false when start before weekday open")
        void weekdayStartBeforeOpen() {
            LocalDateTime start = LocalDateTime.of(2025, 3, 4, 9, 30);
            LocalDateTime end = LocalDateTime.of(2025, 3, 4, 11, 30);
            assertFalse(service.isWithinOpeningHours(start, end));
        }

        @Test
        @DisplayName("returns false when start at or after weekday close")
        void weekdayStartAtClose() {
            LocalDateTime start = LocalDateTime.of(2025, 3, 4, 18, 0);
            LocalDateTime end = LocalDateTime.of(2025, 3, 4, 20, 0);
            assertFalse(service.isWithinOpeningHours(start, end));
        }

        @Test
        @DisplayName("returns false when end after weekday close")
        void weekdayEndAfterClose() {
            LocalDateTime start = LocalDateTime.of(2025, 3, 4, 17, 0);
            LocalDateTime end = LocalDateTime.of(2025, 3, 4, 19, 0);
            assertFalse(service.isWithinOpeningHours(start, end));
        }

        @Test
        @DisplayName("returns true when weekend slot within 10:00–22:00")
        void weekendWithinHours() {
            LocalDateTime start = LocalDateTime.of(2025, 3, 8, 14, 0);  // Saturday 14:00
            LocalDateTime end = LocalDateTime.of(2025, 3, 8, 16, 0);
            assertTrue(service.isWithinOpeningHours(start, end));
        }

        @Test
        @DisplayName("returns true when end exactly at weekday close")
        void endExactlyAtWeekdayClose() {
            LocalDateTime start = LocalDateTime.of(2025, 3, 4, 16, 0);
            LocalDateTime end = LocalDateTime.of(2025, 3, 4, 18, 0);
            assertTrue(service.isWithinOpeningHours(start, end));
        }

        @Test
        @DisplayName("returns false when span crosses into next day and end after close")
        void spanCrossesDayEndAfterClose() {
            LocalDateTime start = LocalDateTime.of(2025, 3, 4, 17, 0);
            LocalDateTime end = LocalDateTime.of(2025, 3, 5, 10, 0);
            assertFalse(service.isWithinOpeningHours(start, end));
        }
    }
}
