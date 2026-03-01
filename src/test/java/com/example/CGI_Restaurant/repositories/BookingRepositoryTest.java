package com.example.CGI_Restaurant.repositories;



import com.example.CGI_Restaurant.config.JpaTestConfig;
import com.example.CGI_Restaurant.domain.entities.Booking;
import com.example.CGI_Restaurant.domain.entities.BookingStatusEnum;
import com.example.CGI_Restaurant.domain.entities.User;
import com.example.CGI_Restaurant.domain.entities.UserRoleEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(JpaTestConfig.class)
@ActiveProfiles("test")
class BookingRepositoryTest {

    @Autowired
    private BookingRepository repository;

    @Autowired
    private UserRepository userRepository;

    private User saveUser(String fullName, String email) {
        return userRepository.save(User.builder()
                .name(fullName)
                .email(email)
                .passwordHash("$2a$10$hashedPasswordForTesting")
                .role(UserRoleEnum.CUSTOMER)
                .build());
    }

    private Booking newBooking(User user, LocalDateTime start, LocalDateTime end, String guestName, String guestEmail, int partySize) {
        return Booking.builder()
                .guestName(guestName)
                .guestEmail(guestEmail)
                .startAt(start)
                .endAt(end)
                .partySize(partySize)
                .status(BookingStatusEnum.CONFIRMED)
                .specialRequests(null)
                .user(user)
                .build();
    }

    @Nested
    @DisplayName("Save and find")
    class SaveAndFind {

        @Test
        @DisplayName("Saves and finds by id")
        void saveAndFindById() {
            User user = saveUser("Gregor Tamm", "gregor.tamm@example.ee");
            LocalDateTime start = LocalDateTime.now().plusDays(1);
            LocalDateTime end = start.plusHours(2);
            Booking booking = repository.save(newBooking(user, start, end, "Gregor Tamm", "gregor.tamm@example.ee", 4));

            assertNotNull(booking.getId());
            assertTrue(repository.findById(booking.getId()).isPresent());
            assertEquals("Gregor Tamm", booking.getGuestName());
            assertEquals(4, booking.getPartySize());
        }

        @Test
        @DisplayName("FindByUserId returns bookings for user")
        void findByUserId() {
            User user = saveUser("Mari Kask", "mari.kask@restaurant.ee");
            repository.save(newBooking(user, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(2), "Mari Kask", "mari.kask@restaurant.ee", 2));

            var page = repository.findByUserId(user.getId(), PageRequest.of(0, 10));
            assertTrue(page.getContent().size() >= 1);
        }

        @Test
        @DisplayName("FindByIdAndUserId returns booking when user owns it")
        void findByIdAndUserId() {
            User user = saveUser("Kati Laas", "kati.laas@example.ee");
            Booking booking = repository.save(newBooking(user, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(2), "Kati Laas", "kati.laas@example.ee", 6));

            Optional<Booking> found = repository.findByIdAndUserId(booking.getId(), user.getId());
            assertTrue(found.isPresent());
        }

        @Test
        @DisplayName("FindByIdAndUserId returns empty when user does not own booking")
        void findByIdAndUserIdReturnsEmptyWhenWrongUser() {
            User toomas = saveUser("Toomas Oja", "toomas.oja@example.ee");
            User liis = saveUser("Liis Mänd", "liis.mand@example.ee");
            Booking booking = repository.save(newBooking(toomas, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(2), "Toomas Oja", "toomas.oja@example.ee", 2));

            Optional<Booking> found = repository.findByIdAndUserId(booking.getId(), liis.getId());
            assertTrue(found.isEmpty());
        }
    }
}
