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

class FeatureTest {

    @Nested
    @DisplayName("Builder")
    class BuilderTests {

        @Test
        @DisplayName("Builder creates feature with all attributes")
        void builderCreatesFeatureWithAllFields() {
            UUID id = UUID.randomUUID();
            LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
            LocalDateTime updatedAt = LocalDateTime.now();

            Feature feature = Feature.builder()
                    .id(id)
                    .code(FeatureCodeEnum.WINDOW)
                    .name("Window view")
                    .bookingPreferences(new ArrayList<>())
                    .createdAt(createdAt)
                    .updatedAt(updatedAt)
                    .build();

            assertNotNull(feature);
            assertEquals(id, feature.getId());
            assertEquals(FeatureCodeEnum.WINDOW, feature.getCode());
            assertEquals("Window view", feature.getName());
            assertNotNull(feature.getBookingPreferences());
            assertTrue(feature.getBookingPreferences().isEmpty());
            assertEquals(createdAt, feature.getCreatedAt());
            assertEquals(updatedAt, feature.getUpdatedAt());
        }

        @Test
        @DisplayName("Builder without ID (JPA generates it)")
        void builderWithoutId() {
            Feature feature = Feature.builder()
                    .code(FeatureCodeEnum.ACCESSIBLE)
                    .name("Wheelchair accessible")
                    .build();

            assertNull(feature.getId());
            assertEquals(FeatureCodeEnum.ACCESSIBLE, feature.getCode());
        }
    }

    @Nested
    @DisplayName("No-args constructor and setters")
    class ConstructorAndSetters {

        @Test
        @DisplayName("No-args constructor creates empty feature")
        void noArgsConstructorCreatesEmptyFeature() {
            Feature feature = new Feature();

            assertNotNull(feature);
            assertNull(feature.getId());
            assertNull(feature.getCode());
            assertNull(feature.getName());
            assertNotNull(feature.getBookingPreferences());
            assertTrue(feature.getBookingPreferences().isEmpty());
        }

        @Test
        @DisplayName("Setters and getters work")
        void settersAndGettersWork() {
            Feature feature = new Feature();
            UUID id = UUID.randomUUID();
            LocalDateTime now = LocalDateTime.now();

            feature.setId(id);
            feature.setCode(FeatureCodeEnum.QUIET);
            feature.setName("Quiet area");
            feature.setCreatedAt(now);
            feature.setUpdatedAt(now);

            assertEquals(id, feature.getId());
            assertEquals(FeatureCodeEnum.QUIET, feature.getCode());
            assertEquals("Quiet area", feature.getName());
            assertEquals(now, feature.getCreatedAt());
        }
    }
}
