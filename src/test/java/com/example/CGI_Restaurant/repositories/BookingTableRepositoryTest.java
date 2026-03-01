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
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(JpaTestConfig.class)
@ActiveProfiles("test")
class BookingTableRepositoryTest {

    @Autowired
    private BookingTableRepository repository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TableEntityRepository tableEntityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private SeatingPlanRepository seatingPlanRepository;

    @Autowired
    private ZoneRepository zoneRepository;

    private TableEntity saveTable() {
        var now = java.time.LocalDateTime.now();
        Restaurant restaurant = restaurantRepository.save(Restaurant.builder()
                .name("Resto Aed")
                .timezone("Europe/Tallinn")
                .email("info@restoaed.ee")
                .phone("+372 6123456")
                .address("Pärnu mnt 1, Tallinn")
                .createdAt(now).updatedAt(now).build());
        SeatingPlan plan = seatingPlanRepository.save(SeatingPlan.builder()
                .name("Peosaal")
                .type(SeatingPlanTypeEnum.FLOOR_1)
                .width(800.0).height(600.0).active(true).version(1).restaurant(restaurant)
                .createdAt(now).updatedAt(now).build());
        Zone zone = zoneRepository.save(Zone.builder()
                .name("Siseala")
                .type(ZoneTypeEnum.INDOOR)
                .color("#3366FF")
                .seatingPlan(plan).createdAt(now).updatedAt(now).build());
        return tableEntityRepository.save(TableEntity.builder()
                .label("Laud 1")
                .capacity(4).minPartySize(2).shape(TableShapeEnum.RECT)
                .x(10.0).y(20.0).width(80.0).height(120.0).rotationDegree(0).active(true)
                .zone(zone).seatingPlan(plan).createdAt(now).updatedAt(now).build());
    }

    private Booking saveBooking(LocalDateTime start, LocalDateTime end, BookingStatusEnum status) {
        User user = userRepository.save(User.builder()
                .name("Gregor Tamm")
                .email("gregor.tamm." + UUID.randomUUID() + "@example.ee")
                .passwordHash("$2a$10$hashedPassword")
                .role(UserRoleEnum.CUSTOMER)
                .build());
        return bookingRepository.save(Booking.builder()
                .guestName("Gregor Tamm")
                .guestEmail("gregor.tamm@example.ee")
                .startAt(start).endAt(end).partySize(4).status(status).user(user).build());
    }

    @Nested
    @DisplayName("Save and find")
    class SaveAndFind {

        @Test
        @DisplayName("Saves and finds by id")
        void saveAndFindById() {
            TableEntity table = saveTable();
            LocalDateTime start = LocalDateTime.now().plusDays(1);
            LocalDateTime end = start.plusHours(2);
            Booking booking = saveBooking(start, end, BookingStatusEnum.CONFIRMED);

            BookingTable bt = repository.save(BookingTable.builder().booking(booking).tableEntity(table).build());
            assertNotNull(bt.getId());
            assertTrue(repository.findById(bt.getId()).isPresent());
        }

        @Test
        @DisplayName("FindTableEntityIdsBookedBetween returns table ids in time range")
        void findTableEntityIdsBookedBetween() {
            TableEntity table = saveTable();
            LocalDateTime start = LocalDateTime.now().plusDays(1);
            LocalDateTime end = start.plusHours(2);
            Booking booking = saveBooking(start, end, BookingStatusEnum.CONFIRMED);
            repository.save(BookingTable.builder().booking(booking).tableEntity(table).build());

            List<UUID> ids = repository.findTableEntityIdsBookedBetween(start.minusMinutes(30), end.plusMinutes(30));
            assertTrue(ids.contains(table.getId()));
        }

        @Test
        @DisplayName("FindTableEntityIdsBookedBetween excludes CANCELLED bookings")
        void findTableEntityIdsBookedBetweenExcludesCancelled() {
            TableEntity table = saveTable();
            LocalDateTime start = LocalDateTime.now().plusDays(1);
            LocalDateTime end = start.plusHours(2);
            Booking cancelled = saveBooking(start, end, BookingStatusEnum.CANCELLED);
            repository.save(BookingTable.builder().booking(cancelled).tableEntity(table).build());

            List<UUID> ids = repository.findTableEntityIdsBookedBetween(start.minusMinutes(30), end.plusMinutes(30));
            assertFalse(ids.contains(table.getId()));
        }
    }
}
