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
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(JpaTestConfig.class)
@ActiveProfiles("test")
class BookingPreferenceRepositoryTest {

    @Autowired
    private BookingPreferenceRepository repository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private FeatureRepository featureRepository;

    @Autowired
    private UserRepository userRepository;

    private Booking saveBooking() {
        User user = userRepository.save(User.builder()
                .name("Mari Kask")
                .email("mari.kask." + UUID.randomUUID() + "@restaurant.ee")
                .passwordHash("$2a$10$hashedPasswordForTesting")
                .role(UserRoleEnum.CUSTOMER)
                .build());
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        return bookingRepository.save(Booking.builder()
                .guestName("Mari Kask")
                .guestEmail("mari.kask@restaurant.ee")
                .startAt(start).endAt(start.plusHours(2)).partySize(2)
                .status(BookingStatusEnum.CONFIRMED).user(user).build());
    }

    @Nested
    @DisplayName("Save and find")
    class SaveAndFind {

        @Test
        @DisplayName("Saves and finds by id")
        void saveAndFindById() {
            Booking booking = saveBooking();
            Feature feature = featureRepository.save(Feature.builder()
                    .code(FeatureCodeEnum.WINDOW)
                    .name("Akna vaade")
                    .build());

            BookingPreference pref = repository.save(BookingPreference.builder()
                    .priority(PreferencePriorityEnum.HIGH)
                    .booking(booking)
                    .feature(feature)
                    .build());

            assertNotNull(pref.getId());
            Optional<BookingPreference> found = repository.findById(pref.getId());
            assertTrue(found.isPresent());
            assertEquals(PreferencePriorityEnum.HIGH, found.get().getPriority());
        }

        @Test
        @DisplayName("FindById returns empty when not found")
        void findByIdReturnsEmptyWhenNotFound() {
            assertTrue(repository.findById(UUID.randomUUID()).isEmpty());
        }
    }
}
