package com.example.CGI_Restaurant.services.impl;

import com.example.CGI_Restaurant.domain.entities.*;
import com.example.CGI_Restaurant.domain.createRequests.CreateBookingPreferenceRequest;
import com.example.CGI_Restaurant.domain.createRequests.CreateBookingRequest;
import com.example.CGI_Restaurant.domain.createRequests.CreateBookingTableRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateBookingPreferenceRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateBookingRequest;
import com.example.CGI_Restaurant.exceptions.RestaurantBookingException;
import com.example.CGI_Restaurant.exceptions.notFoundExceptions.BookingNotFoundException;
import com.example.CGI_Restaurant.exceptions.notFoundExceptions.BookingPreferenceNotFoundException;
import com.example.CGI_Restaurant.exceptions.updateException.BookingUpdateException;
import com.example.CGI_Restaurant.repositories.BookingRepository;
import com.example.CGI_Restaurant.repositories.BookingTableRepository;
import com.example.CGI_Restaurant.repositories.UserRepository;
import com.example.CGI_Restaurant.services.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingPreferenceService bookingPreferenceService;

    @Mock
    private BookingTableService bookingTableService;

    @Mock
    private BookingTableRepository bookingTableRepository;

    @Mock
    private EntityManager entityManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private QrCodeService qrCodeService;

    @Mock
    private EmailService emailService;

    @Mock
    private RestaurantHoursService restaurantHoursService;

    @Mock
    private TableEntityService tableEntityService;

    @InjectMocks
    private BookingServiceImpl service;

    private static CreateBookingRequest minimalValidRequest(int durationHours) {
        LocalDateTime start = LocalDateTime.of(2025, 4, 10, 12, 0);
        LocalDateTime end = start.plusHours(durationHours);
        User user = User.builder().id(UUID.randomUUID()).name("Mari Kask").email("mari@example.ee").passwordHash("x").build();
        CreateBookingRequest req = new CreateBookingRequest();
        req.setGuestName("Mari Kask");
        req.setGuestEmail("mari@example.ee");
        req.setStartAt(start);
        req.setEndAt(end);
        req.setPartySize(2);
        req.setStatus(BookingStatusEnum.PENDING);
        req.setQrToken("");
        req.setSpecialRequests(null);
        req.setUser(user);
        req.setBookingPreferences(List.of());
        req.setBookingTables(List.of());
        return req;
    }

    @Nested
    @DisplayName("createBooking")
    class CreateBooking {

        @Test
        @DisplayName("throws when outside opening hours")
        void throwsWhenOutsideHours() {
            when(restaurantHoursService.isWithinOpeningHours(any(), any())).thenReturn(false);
            CreateBookingRequest request = minimalValidRequest(2);

            RestaurantBookingException ex = assertThrows(RestaurantBookingException.class, () -> service.createBooking(request));
            assertTrue(ex.getMessage().toLowerCase().contains("opening hours"));
            verify(bookingRepository, never()).save(any());
        }

        @Test
        @DisplayName("throws when duration does not match configured hours")
        void throwsWhenWrongDuration() {
            when(restaurantHoursService.getBookingDurationHours()).thenReturn(2);
            when(restaurantHoursService.isWithinOpeningHours(any(), any())).thenReturn(true);
            CreateBookingRequest request = minimalValidRequest(3);

            RestaurantBookingException ex = assertThrows(RestaurantBookingException.class, () -> service.createBooking(request));
            assertTrue(ex.getMessage().toLowerCase().contains("duration"));
            verify(bookingRepository, never()).save(any());
        }

        @Test
        @DisplayName("validates tables adjacent when multiple tables requested")
        void validatesAdjacentWhenMultipleTables() {
            when(restaurantHoursService.getBookingDurationHours()).thenReturn(2);
            when(restaurantHoursService.isWithinOpeningHours(any(), any())).thenReturn(true);
            UUID t1 = UUID.randomUUID();
            UUID t2 = UUID.randomUUID();
            CreateBookingRequest request = minimalValidRequest(2);
            request.setBookingTables(List.of(createTableReq(t1), createTableReq(t2)));
            doThrow(new RestaurantBookingException("Chosen tables must be nearby to each other."))
                    .when(tableEntityService).validateTablesAdjacent(anySet());

            RestaurantBookingException ex = assertThrows(RestaurantBookingException.class, () -> service.createBooking(request));
            assertTrue(ex.getMessage().toLowerCase().contains("nearby") || ex.getMessage().contains("nearby"));
            verify(bookingRepository, never()).save(any());
        }

        @Test
        @DisplayName("throws when one of selected tables already booked")
        void throwsWhenTableAlreadyBooked() {
            when(restaurantHoursService.getBookingDurationHours()).thenReturn(2);
            when(restaurantHoursService.isWithinOpeningHours(any(), any())).thenReturn(true);
            UUID tableId = UUID.randomUUID();
            CreateBookingRequest request = minimalValidRequest(2);
            request.setBookingTables(List.of(createTableReq(tableId)));
            when(bookingTableRepository.findTableEntityIdsBookedBetween(any(), any())).thenReturn(List.of(tableId));

            RestaurantBookingException ex = assertThrows(RestaurantBookingException.class, () -> service.createBooking(request));
            assertTrue(ex.getMessage().toLowerCase().contains("already booked"));
            verify(bookingRepository, never()).save(any());
        }

        @Test
        @DisplayName("saves booking and creates preferences, tables, QR and sends email when valid")
        void successCreatesBookingAndDependencies() {
            int durationHours = 2;
            when(restaurantHoursService.getBookingDurationHours()).thenReturn(durationHours);
            when(restaurantHoursService.isWithinOpeningHours(any(), any())).thenReturn(true);

            UUID bookingId = UUID.randomUUID();
            Booking savedBooking = Booking.builder().id(bookingId).guestName("Mari Kask").guestEmail("mari@example.ee")
                    .qrCodes(new ArrayList<>()).bookingPreferences(new ArrayList<>()).bookingTables(new ArrayList<>()).build();
            when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);

            QrCode qr = QrCode.builder().id(UUID.randomUUID()).value("base64").status(QrCodeStatusEnum.ACTIVE).booking(savedBooking).build();
            when(qrCodeService.generateQrCode(any(Booking.class))).thenReturn(qr);

            CreateBookingRequest request = minimalValidRequest(durationHours);
            UUID featureId = UUID.randomUUID();
            CreateBookingPreferenceRequest prefReq = new CreateBookingPreferenceRequest();
            prefReq.setFeatureId(featureId);
            prefReq.setPriority(PreferencePriorityEnum.HIGH);
            request.setBookingPreferences(List.of(prefReq));

            Booking result = service.createBooking(request);

            assertNotNull(result);
            verify(bookingRepository).save(any(Booking.class));
            verify(bookingPreferenceService).create(argThat(r -> r.getBookingId().equals(bookingId) && r.getFeatureId().equals(featureId)));
            verify(qrCodeService).generateQrCode(any(Booking.class));
            verify(emailService).sendBookingConfirmation(eq(savedBooking), eq("base64"));
        }

        @Test
        @DisplayName("single table does not trigger adjacent validation")
        void singleTableNoAdjacentValidation() {
            when(restaurantHoursService.getBookingDurationHours()).thenReturn(2);
            when(restaurantHoursService.isWithinOpeningHours(any(), any())).thenReturn(true);
            UUID tableId = UUID.randomUUID();
            CreateBookingRequest request = minimalValidRequest(2);
            request.setBookingTables(List.of(createTableReq(tableId)));
            when(bookingTableRepository.findTableEntityIdsBookedBetween(any(), any())).thenReturn(List.of());
            Booking saved = Booking.builder().id(UUID.randomUUID()).bookingTables(new ArrayList<>()).bookingPreferences(new ArrayList<>()).qrCodes(new ArrayList<>()).build();
            when(bookingRepository.save(any(Booking.class))).thenReturn(saved);
            when(qrCodeService.generateQrCode(any(Booking.class))).thenReturn(QrCode.builder().id(UUID.randomUUID()).value("qr").status(QrCodeStatusEnum.ACTIVE).booking(saved).build());

            service.createBooking(request);

            verify(tableEntityService, never()).validateTablesAdjacent(anySet());
            verify(bookingTableService).create(argThat(r -> r.getTableEntityId().equals(tableId)));
        }

        @Test
        @DisplayName("null bookingTables treated as empty list")
        void nullBookingTablesAsEmpty() {
            when(restaurantHoursService.getBookingDurationHours()).thenReturn(2);
            when(restaurantHoursService.isWithinOpeningHours(any(), any())).thenReturn(true);
            CreateBookingRequest request = minimalValidRequest(2);
            request.setBookingTables(null);
            Booking saved = Booking.builder().id(UUID.randomUUID()).bookingTables(new ArrayList<>()).bookingPreferences(new ArrayList<>()).qrCodes(new ArrayList<>()).build();
            when(bookingRepository.save(any(Booking.class))).thenReturn(saved);
            when(qrCodeService.generateQrCode(any(Booking.class))).thenReturn(QrCode.builder().id(UUID.randomUUID()).value("qr").status(QrCodeStatusEnum.ACTIVE).booking(saved).build());

            assertDoesNotThrow(() -> service.createBooking(request));
            verify(bookingTableService, never()).create(any());
        }

        @Test
        @DisplayName("null bookingPreferences treated as empty")
        void nullBookingPreferencesAsEmpty() {
            when(restaurantHoursService.getBookingDurationHours()).thenReturn(2);
            when(restaurantHoursService.isWithinOpeningHours(any(), any())).thenReturn(true);
            CreateBookingRequest request = minimalValidRequest(2);
            request.setBookingPreferences(null);
            Booking saved = Booking.builder().id(UUID.randomUUID()).bookingTables(new ArrayList<>()).bookingPreferences(new ArrayList<>()).qrCodes(new ArrayList<>()).build();
            when(bookingRepository.save(any(Booking.class))).thenReturn(saved);
            when(qrCodeService.generateQrCode(any(Booking.class))).thenReturn(QrCode.builder().id(UUID.randomUUID()).value("qr").status(QrCodeStatusEnum.ACTIVE).booking(saved).build());

            assertDoesNotThrow(() -> service.createBooking(request));
            verify(bookingPreferenceService, never()).create(any());
        }

        @Test
        @DisplayName("null qrToken stored as empty string")
        void nullQrTokenStoredAsEmpty() {
            when(restaurantHoursService.getBookingDurationHours()).thenReturn(2);
            when(restaurantHoursService.isWithinOpeningHours(any(), any())).thenReturn(true);
            CreateBookingRequest request = minimalValidRequest(2);
            request.setQrToken(null);
            Booking saved = Booking.builder().id(UUID.randomUUID()).bookingTables(new ArrayList<>()).bookingPreferences(new ArrayList<>()).qrCodes(new ArrayList<>()).build();
            when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> {
                Booking b = inv.getArgument(0);
                assertNotNull(b.getQrToken());
                assertEquals("", b.getQrToken());
                return saved;
            });
            when(qrCodeService.generateQrCode(any(Booking.class))).thenReturn(QrCode.builder().id(UUID.randomUUID()).value("qr").status(QrCodeStatusEnum.ACTIVE).booking(saved).build());

            service.createBooking(request);
            verify(bookingRepository).save(argThat(b -> "".equals(b.getQrToken())));
        }

        @Test
        @DisplayName("duration zero hours throws")
        void durationZeroThrows() {
            when(restaurantHoursService.getBookingDurationHours()).thenReturn(2);
            when(restaurantHoursService.isWithinOpeningHours(any(), any())).thenReturn(true);
            CreateBookingRequest request = minimalValidRequest(2);
            request.setEndAt(request.getStartAt());

            RestaurantBookingException ex = assertThrows(RestaurantBookingException.class, () -> service.createBooking(request));
            assertTrue(ex.getMessage().toLowerCase().contains("duration"));
        }

        @Test
        @DisplayName("overlapping slot with different table does not block")
        void overlappingDifferentTableSucceeds() {
            when(restaurantHoursService.getBookingDurationHours()).thenReturn(2);
            when(restaurantHoursService.isWithinOpeningHours(any(), any())).thenReturn(true);
            UUID myTable = UUID.randomUUID();
            UUID otherBookedTable = UUID.randomUUID();
            CreateBookingRequest request = minimalValidRequest(2);
            request.setBookingTables(List.of(createTableReq(myTable)));
            when(bookingTableRepository.findTableEntityIdsBookedBetween(any(), any())).thenReturn(List.of(otherBookedTable));
            Booking saved = Booking.builder().id(UUID.randomUUID()).bookingTables(new ArrayList<>()).bookingPreferences(new ArrayList<>()).qrCodes(new ArrayList<>()).build();
            when(bookingRepository.save(any(Booking.class))).thenReturn(saved);
            when(qrCodeService.generateQrCode(any(Booking.class))).thenReturn(QrCode.builder().id(UUID.randomUUID()).value("qr").status(QrCodeStatusEnum.ACTIVE).booking(saved).build());

            assertDoesNotThrow(() -> service.createBooking(request));
            verify(bookingRepository).save(any(Booking.class));
        }
    }

    @Nested
    @DisplayName("listBookingForCustomer")
    class ListBookingForCustomer {

        @Test
        @DisplayName("returns page from repository")
        void returnsPage() {
            UUID customerId = UUID.randomUUID();
            Pageable pageable = PageRequest.of(0, 10);
            Booking b = Booking.builder().id(UUID.randomUUID()).build();
            when(bookingRepository.findByUserId(customerId, pageable)).thenReturn(new PageImpl<>(List.of(b), pageable, 1));
            Page<Booking> result = service.listBookingForCustomer(customerId, pageable);
            assertEquals(1, result.getContent().size());
        }

        @Test
        @DisplayName("returns empty page when customer has no bookings")
        void returnsEmptyPage() {
            UUID customerId = UUID.randomUUID();
            Pageable pageable = PageRequest.of(0, 10);
            when(bookingRepository.findByUserId(customerId, pageable)).thenReturn(new PageImpl<>(List.of(), pageable, 0));
            Page<Booking> result = service.listBookingForCustomer(customerId, pageable);
            assertTrue(result.getContent().isEmpty());
            assertEquals(0, result.getTotalElements());
        }

        @Test
        @DisplayName("passes correct pageable to repository")
        void passesPageable() {
            UUID customerId = UUID.randomUUID();
            Pageable pageable = PageRequest.of(2, 5);
            when(bookingRepository.findByUserId(customerId, pageable)).thenReturn(new PageImpl<>(List.of(), pageable, 0));
            service.listBookingForCustomer(customerId, pageable);
            verify(bookingRepository).findByUserId(customerId, pageable);
        }
    }

    @Nested
    @DisplayName("listBookingsForAdmin")
    class ListBookingsForAdmin {

        @Test
        @DisplayName("returns page from repository")
        void returnsPage() {
            Pageable pageable = PageRequest.of(0, 10);
            when(bookingRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(), pageable, 0));
            Page<Booking> result = service.listBookingsForAdmin(pageable);
            assertNotNull(result);
        }
    }

    @Nested
    @DisplayName("getBookingForCustomer")
    class GetBookingForCustomer {

        @Test
        @DisplayName("returns present when found for customer")
        void returnsPresentWhenFound() {
            UUID id = UUID.randomUUID();
            UUID customerId = UUID.randomUUID();
            Booking b = Booking.builder().id(id).build();
            when(bookingRepository.findByIdAndUserId(id, customerId)).thenReturn(Optional.of(b));
            assertTrue(service.getBookingForCustomer(id, customerId).isPresent());
        }

        @Test
        @DisplayName("returns empty when not found")
        void returnsEmptyWhenNotFound() {
            when(bookingRepository.findByIdAndUserId(any(UUID.class), any(UUID.class))).thenReturn(Optional.empty());
            assertTrue(service.getBookingForCustomer(UUID.randomUUID(), UUID.randomUUID()).isEmpty());
        }
    }

    @Nested
    @DisplayName("getBooking")
    class GetBooking {

        @Test
        @DisplayName("returns present when found")
        void returnsPresentWhenFound() {
            UUID id = UUID.randomUUID();
            Booking b = Booking.builder().id(id).build();
            when(bookingRepository.findById(id)).thenReturn(Optional.of(b));
            assertTrue(service.getBooking(id).isPresent());
            assertEquals(id, service.getBooking(id).get().getId());
        }

        @Test
        @DisplayName("returns empty when not found")
        void returnsEmptyWhenNotFound() {
            when(bookingRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
            assertTrue(service.getBooking(UUID.randomUUID()).isEmpty());
        }
    }

    @Nested
    @DisplayName("updateBookingForCustomer")
    class UpdateBookingForCustomer {

        @Test
        @DisplayName("throws BookingUpdateException when request id is null")
        void throwsWhenIdNull() {
            UpdateBookingRequest request = new UpdateBookingRequest();
            request.setId(null);
            assertThrows(BookingUpdateException.class, () -> service.updateBookingForCustomer(UUID.randomUUID(), UUID.randomUUID(), request));
            verify(bookingRepository, never()).save(any());
        }

        @Test
        @DisplayName("throws BookingUpdateException when path id and request id differ")
        void throwsWhenIdMismatch() {
            UpdateBookingRequest request = new UpdateBookingRequest();
            request.setId(UUID.randomUUID());
            assertThrows(BookingUpdateException.class, () -> service.updateBookingForCustomer(UUID.randomUUID(), UUID.randomUUID(), request));
        }

        @Test
        @DisplayName("throws BookingNotFoundException when booking not found for customer")
        void throwsWhenNotFound() {
            UUID id = UUID.randomUUID();
            UUID customerId = UUID.randomUUID();
            UpdateBookingRequest request = new UpdateBookingRequest();
            request.setId(id);
            request.setBookingPreferences(List.of());
            when(bookingRepository.findByIdAndUserId(id, customerId)).thenReturn(Optional.empty());
            assertThrows(BookingNotFoundException.class, () -> service.updateBookingForCustomer(id, customerId, request));
            verify(bookingRepository, never()).save(any());
        }

        @Test
        @DisplayName("updates and saves when found and id matches")
        void updatesWhenFound() {
            UUID id = UUID.randomUUID();
            UUID customerId = UUID.randomUUID();
            Booking existing = Booking.builder().id(id).guestName("Old").guestEmail("old@ee").bookingPreferences(new ArrayList<>()).build();
            when(bookingRepository.findByIdAndUserId(id, customerId)).thenReturn(Optional.of(existing));
            when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

            UpdateBookingRequest request = new UpdateBookingRequest();
            request.setId(id);
            request.setGuestName("Mari Kask");
            request.setGuestEmail("mari@example.ee");
            request.setStartAt(LocalDateTime.of(2025, 5, 1, 18, 0));
            request.setEndAt(LocalDateTime.of(2025, 5, 1, 20, 0));
            request.setPartySize(4);
            request.setStatus(BookingStatusEnum.CONFIRMED);
            request.setQrToken("");
            request.setSpecialRequests("Vegan");
            request.setBookingPreferences(List.of());

            Booking result = service.updateBookingForCustomer(id, customerId, request);

            assertEquals("Mari Kask", result.getGuestName());
            assertEquals("mari@example.ee", result.getGuestEmail());
            assertEquals(4, result.getPartySize());
            assertEquals(BookingStatusEnum.CONFIRMED, result.getStatus());
            verify(bookingRepository).save(existing);
        }

        @Test
        @DisplayName("throws BookingPreferenceNotFoundException when preference id not in booking")
        void throwsWhenPreferenceIdNotFound() {
            UUID id = UUID.randomUUID();
            UUID customerId = UUID.randomUUID();
            UUID unknownPrefId = UUID.randomUUID();
            Booking existing = Booking.builder().id(id).bookingPreferences(new ArrayList<>()).build();
            when(bookingRepository.findByIdAndUserId(id, customerId)).thenReturn(Optional.of(existing));

            UpdateBookingPreferenceRequest prefReq = new UpdateBookingPreferenceRequest();
            prefReq.setId(unknownPrefId);
            prefReq.setPriority(PreferencePriorityEnum.MEDIUM);
            UpdateBookingRequest request = new UpdateBookingRequest();
            request.setId(id);
            request.setGuestName("A");
            request.setGuestEmail("a@b.ee");
            request.setStartAt(LocalDateTime.now());
            request.setEndAt(LocalDateTime.now().plusHours(2));
            request.setPartySize(2);
            request.setStatus(BookingStatusEnum.PENDING);
            request.setQrToken("");
            request.setBookingPreferences(List.of(prefReq));

            assertThrows(BookingPreferenceNotFoundException.class, () -> service.updateBookingForCustomer(id, customerId, request));
        }

        @Test
        @DisplayName("removing all preferences leaves empty list")
        void removeAllPreferences() {
            UUID id = UUID.randomUUID();
            UUID customerId = UUID.randomUUID();
            UUID prefId = UUID.randomUUID();
            BookingPreference pref = new BookingPreference();
            pref.setId(prefId);
            pref.setPriority(PreferencePriorityEnum.LOW);
            Booking existing = Booking.builder().id(id).guestName("X").guestEmail("x@x.ee").bookingPreferences(new ArrayList<>(List.of(pref))).build();
            when(bookingRepository.findByIdAndUserId(id, customerId)).thenReturn(Optional.of(existing));
            when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

            UpdateBookingRequest request = new UpdateBookingRequest();
            request.setId(id);
            request.setGuestName("X");
            request.setGuestEmail("x@x.ee");
            request.setStartAt(LocalDateTime.now());
            request.setEndAt(LocalDateTime.now().plusHours(2));
            request.setPartySize(2);
            request.setStatus(BookingStatusEnum.PENDING);
            request.setQrToken("");
            request.setBookingPreferences(List.of());

            Booking result = service.updateBookingForCustomer(id, customerId, request);
            assertTrue(result.getBookingPreferences().isEmpty());
        }

        @Test
        @DisplayName("add new preference with null id")
        void addNewPreferenceWithNullId() {
            UUID id = UUID.randomUUID();
            UUID customerId = UUID.randomUUID();
            Booking existing = Booking.builder().id(id).guestName("X").guestEmail("x@x.ee").bookingPreferences(new ArrayList<>()).build();
            when(bookingRepository.findByIdAndUserId(id, customerId)).thenReturn(Optional.of(existing));
            when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

            UpdateBookingPreferenceRequest newPref = new UpdateBookingPreferenceRequest();
            newPref.setId(null);
            newPref.setPriority(PreferencePriorityEnum.HIGH);
            UpdateBookingRequest request = new UpdateBookingRequest();
            request.setId(id);
            request.setGuestName("X");
            request.setGuestEmail("x@x.ee");
            request.setStartAt(LocalDateTime.now());
            request.setEndAt(LocalDateTime.now().plusHours(2));
            request.setPartySize(2);
            request.setStatus(BookingStatusEnum.PENDING);
            request.setQrToken("");
            request.setBookingPreferences(List.of(newPref));

            Booking result = service.updateBookingForCustomer(id, customerId, request);
            assertEquals(1, result.getBookingPreferences().size());
            assertNull(result.getBookingPreferences().get(0).getId());
            assertEquals(PreferencePriorityEnum.HIGH, result.getBookingPreferences().get(0).getPriority());
        }

        @Test
        @DisplayName("null qrToken in update stored as empty string")
        void updateNullQrTokenAsEmpty() {
            UUID id = UUID.randomUUID();
            UUID customerId = UUID.randomUUID();
            Booking existing = Booking.builder().id(id).guestName("X").guestEmail("x@x.ee").qrToken("old").bookingPreferences(new ArrayList<>()).build();
            when(bookingRepository.findByIdAndUserId(id, customerId)).thenReturn(Optional.of(existing));
            when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

            UpdateBookingRequest request = new UpdateBookingRequest();
            request.setId(id);
            request.setGuestName("X");
            request.setGuestEmail("x@x.ee");
            request.setStartAt(LocalDateTime.now());
            request.setEndAt(LocalDateTime.now().plusHours(2));
            request.setPartySize(2);
            request.setStatus(BookingStatusEnum.PENDING);
            request.setQrToken(null);
            request.setBookingPreferences(List.of());

            Booking result = service.updateBookingForCustomer(id, customerId, request);
            assertEquals("", result.getQrToken());
        }
    }

    @Nested
    @DisplayName("updateBooking")
    class UpdateBooking {

        @Test
        @DisplayName("throws BookingUpdateException when request id is null")
        void throwsWhenIdNull() {
            UpdateBookingRequest request = new UpdateBookingRequest();
            request.setId(null);
            assertThrows(BookingUpdateException.class, () -> service.updateBooking(UUID.randomUUID(), request));
        }

        @Test
        @DisplayName("throws BookingNotFoundException when booking not found")
        void throwsWhenNotFound() {
            UUID id = UUID.randomUUID();
            UpdateBookingRequest request = new UpdateBookingRequest();
            request.setId(id);
            request.setBookingPreferences(List.of());
            when(bookingRepository.findById(id)).thenReturn(Optional.empty());
            assertThrows(BookingNotFoundException.class, () -> service.updateBooking(id, request));
        }

        @Test
        @DisplayName("updates and saves when found")
        void updatesWhenFound() {
            UUID id = UUID.randomUUID();
            Booking existing = Booking.builder().id(id).guestName("X").guestEmail("x@x.ee").bookingPreferences(new ArrayList<>()).build();
            when(bookingRepository.findById(id)).thenReturn(Optional.of(existing));
            when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

            UpdateBookingRequest request = new UpdateBookingRequest();
            request.setId(id);
            request.setGuestName("Gregor");
            request.setGuestEmail("gregor@example.ee");
            request.setStartAt(LocalDateTime.now());
            request.setEndAt(LocalDateTime.now().plusHours(2));
            request.setPartySize(2);
            request.setStatus(BookingStatusEnum.CONFIRMED);
            request.setQrToken("");
            request.setBookingPreferences(List.of());

            Booking result = service.updateBooking(id, request);
            assertEquals("Gregor", result.getGuestName());
            verify(bookingRepository).save(existing);
        }

        @Test
        @DisplayName("updates existing preference priority")
        void updatesExistingPreferencePriority() {
            UUID id = UUID.randomUUID();
            UUID prefId = UUID.randomUUID();
            BookingPreference pref = new BookingPreference();
            pref.setId(prefId);
            pref.setPriority(PreferencePriorityEnum.LOW);
            Booking existing = Booking.builder().id(id).guestName("X").guestEmail("x@x.ee").bookingPreferences(new ArrayList<>(List.of(pref))).build();
            when(bookingRepository.findById(id)).thenReturn(Optional.of(existing));
            when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

            UpdateBookingPreferenceRequest prefReq = new UpdateBookingPreferenceRequest();
            prefReq.setId(prefId);
            prefReq.setPriority(PreferencePriorityEnum.HIGH);
            UpdateBookingRequest request = new UpdateBookingRequest();
            request.setId(id);
            request.setGuestName("X");
            request.setGuestEmail("x@x.ee");
            request.setStartAt(LocalDateTime.now());
            request.setEndAt(LocalDateTime.now().plusHours(2));
            request.setPartySize(2);
            request.setStatus(BookingStatusEnum.PENDING);
            request.setQrToken("");
            request.setBookingPreferences(List.of(prefReq));

            Booking result = service.updateBooking(id, request);
            assertEquals(1, result.getBookingPreferences().size());
            assertEquals(PreferencePriorityEnum.HIGH, result.getBookingPreferences().get(0).getPriority());
        }
    }

    @Nested
    @DisplayName("deleteBooking")
    class DeleteBooking {

        @Test
        @DisplayName("deletes when exists")
        void deletesWhenExists() {
            UUID id = UUID.randomUUID();
            Booking b = Booking.builder().id(id).build();
            when(bookingRepository.findById(id)).thenReturn(Optional.of(b));
            service.deleteBooking(id);
            verify(bookingRepository).delete(b);
        }

        @Test
        @DisplayName("throws BookingNotFoundException when not found")
        void throwsWhenNotFound() {
            UUID id = UUID.randomUUID();
            when(bookingRepository.findById(id)).thenReturn(Optional.empty());
            assertThrows(BookingNotFoundException.class, () -> service.deleteBooking(id));
            verify(bookingRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("deleteBookingForCustomer")
    class DeleteBookingForCustomer {

        @Test
        @DisplayName("deletes when exists for customer")
        void deletesWhenExists() {
            UUID id = UUID.randomUUID();
            UUID customerId = UUID.randomUUID();
            Booking b = Booking.builder().id(id).build();
            when(bookingRepository.findByIdAndUserId(id, customerId)).thenReturn(Optional.of(b));
            service.deleteBookingForCustomer(id, customerId);
            verify(bookingRepository).delete(b);
        }

        @Test
        @DisplayName("throws BookingNotFoundException when not found or not customer's")
        void throwsWhenNotFound() {
            UUID id = UUID.randomUUID();
            UUID customerId = UUID.randomUUID();
            when(bookingRepository.findByIdAndUserId(id, customerId)).thenReturn(Optional.empty());
            assertThrows(BookingNotFoundException.class, () -> service.deleteBookingForCustomer(id, customerId));
            verify(bookingRepository, never()).delete(any());
        }
    }

    private static CreateBookingTableRequest createTableReq(UUID tableEntityId) {
        CreateBookingTableRequest r = new CreateBookingTableRequest();
        r.setTableEntityId(tableEntityId);
        r.setBookingId(UUID.randomUUID());
        return r;
    }
}
