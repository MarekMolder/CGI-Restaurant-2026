package com.example.CGI_Restaurant.mappers;

import com.example.CGI_Restaurant.domain.dtos.createRequests.CreateBookingRequestDto;
import com.example.CGI_Restaurant.domain.dtos.createResponses.CreateBookingResponseDto;
import com.example.CGI_Restaurant.domain.dtos.getResponses.GetBookingDetailsResponseDto;
import com.example.CGI_Restaurant.domain.dtos.listResponses.ListBookingResponseDto;
import com.example.CGI_Restaurant.domain.dtos.updateRequests.UpdateBookingRequestDto;
import com.example.CGI_Restaurant.domain.dtos.updateResponses.UpdateBookingResponseDto;
import com.example.CGI_Restaurant.domain.entities.Booking;
import com.example.CGI_Restaurant.domain.entities.BookingStatusEnum;
import com.example.CGI_Restaurant.domain.createRequests.CreateBookingRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateBookingRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Import(com.example.CGI_Restaurant.TestMailConfig.class)
class BookingMapperTest {

    @Autowired
    private BookingMapper bookingMapper;

    private static Booking createBooking(UUID id, String guestName, BookingStatusEnum status) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.plusHours(1);
        LocalDateTime end = now.plusHours(2);
        return Booking.builder()
                .id(id)
                .guestName(guestName)
                .guestEmail("guest@example.com")
                .startAt(start)
                .endAt(end)
                .partySize(4)
                .status(status)
                .qrToken("qr-123")
                .specialRequests("Window seat")
                .bookingPreferences(new ArrayList<>())
                .bookingTables(new ArrayList<>())
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    @Nested
    @DisplayName("fromDto CreateBookingRequestDto")
    class FromDtoCreate {

        @Test
        @DisplayName("maps CreateBookingRequestDto to CreateBookingRequest")
        void mapsCreateDtoToRequest() {
            LocalDateTime start = LocalDateTime.now().plusDays(1);
            LocalDateTime end = start.plusHours(2);
            CreateBookingRequestDto dto = new CreateBookingRequestDto();
            dto.setGuestName("Jane Doe");
            dto.setGuestEmail("jane@example.com");
            dto.setStartAt(start);
            dto.setEndAt(end);
            dto.setPartySize(2);
            dto.setStatus(BookingStatusEnum.PENDING);
            dto.setQrToken(null);
            dto.setSpecialRequests("Quiet table");
            dto.setBookingPreferences(new ArrayList<>());
            dto.setBookingTables(new ArrayList<>());

            CreateBookingRequest request = bookingMapper.fromDto(dto);

            assertNotNull(request);
            assertEquals("Jane Doe", request.getGuestName());
            assertEquals("jane@example.com", request.getGuestEmail());
            assertEquals(start, request.getStartAt());
            assertEquals(end, request.getEndAt());
            assertEquals(2, request.getPartySize());
            assertEquals(BookingStatusEnum.PENDING, request.getStatus());
            assertEquals("Quiet table", request.getSpecialRequests());
        }

        @Test
        @DisplayName("returns null when dto is null")
        void returnsNullWhenDtoIsNull() {
            assertNull(bookingMapper.fromDto((CreateBookingRequestDto) null));
        }
    }

    @Nested
    @DisplayName("toDto")
    class ToDto {

        @Test
        @DisplayName("maps Booking to CreateBookingResponseDto")
        void mapsToCreateResponseDto() {
            UUID id = UUID.randomUUID();
            Booking booking = createBooking(id, "John", BookingStatusEnum.CONFIRMED);

            CreateBookingResponseDto dto = bookingMapper.toDto(booking);

            assertNotNull(dto);
            assertEquals(id, dto.getId());
            assertEquals("John", dto.getGuestName());
            assertEquals(BookingStatusEnum.CONFIRMED, dto.getStatus());
        }

        @Test
        @DisplayName("returns null when entity is null")
        void returnsNullWhenEntityIsNull() {
            assertNull(bookingMapper.toDto(null));
        }
    }

    @Nested
    @DisplayName("toListBookingResponseDto")
    class ToListBookingResponseDto {

        @Test
        @DisplayName("maps Booking to ListBookingResponseDto")
        void mapsToListDto() {
            UUID id = UUID.randomUUID();
            Booking booking = createBooking(id, "Alice", BookingStatusEnum.PENDING);

            ListBookingResponseDto dto = bookingMapper.toListBookingResponseDto(booking);

            assertNotNull(dto);
            assertEquals(id, dto.getId());
            assertEquals("Alice", dto.getGuestName());
        }

        @Test
        @DisplayName("returns null when entity is null")
        void returnsNullWhenEntityIsNull() {
            assertNull(bookingMapper.toListBookingResponseDto(null));
        }
    }

    @Nested
    @DisplayName("toGetBookingDetailsResponseDto")
    class ToGetBookingDetailsResponseDto {

        @Test
        @DisplayName("maps Booking to GetBookingDetailsResponseDto")
        void mapsToGetDetailsDto() {
            UUID id = UUID.randomUUID();
            Booking booking = createBooking(id, "Bob", BookingStatusEnum.COMPLETED);

            GetBookingDetailsResponseDto dto = bookingMapper.toGetBookingDetailsResponseDto(booking);

            assertNotNull(dto);
            assertEquals(id, dto.getId());
            assertEquals("Bob", dto.getGuestName());
            assertEquals(BookingStatusEnum.COMPLETED, dto.getStatus());
        }

        @Test
        @DisplayName("returns null when entity is null")
        void returnsNullWhenEntityIsNull() {
            assertNull(bookingMapper.toGetBookingDetailsResponseDto(null));
        }
    }

    @Nested
    @DisplayName("fromDto UpdateBookingRequestDto")
    class FromDtoUpdate {

        @Test
        @DisplayName("maps UpdateBookingRequestDto to UpdateBookingRequest")
        void mapsUpdateDtoToRequest() {
            UUID id = UUID.randomUUID();
            LocalDateTime start = LocalDateTime.now().plusDays(2);
            LocalDateTime end = start.plusHours(1);
            UpdateBookingRequestDto dto = new UpdateBookingRequestDto();
            dto.setId(id);
            dto.setGuestName("Updated Guest");
            dto.setGuestEmail("updated@example.com");
            dto.setStartAt(start);
            dto.setEndAt(end);
            dto.setPartySize(6);
            dto.setStatus(BookingStatusEnum.CANCELLED);
            dto.setQrToken("qr-updated");
            dto.setSpecialRequests("No requests");
            dto.setBookingPreferences(new ArrayList<>());
            dto.setBookingTables(new ArrayList<>());

            UpdateBookingRequest request = bookingMapper.fromDto(dto);

            assertNotNull(request);
            assertEquals(id, request.getId());
            assertEquals("Updated Guest", request.getGuestName());
            assertEquals(BookingStatusEnum.CANCELLED, request.getStatus());
        }

        @Test
        @DisplayName("returns null when dto is null")
        void returnsNullWhenDtoIsNull() {
            assertNull(bookingMapper.fromDto((UpdateBookingRequestDto) null));
        }
    }

    @Nested
    @DisplayName("toUpdateBookingResponseDto")
    class ToUpdateBookingResponseDto {

        @Test
        @DisplayName("maps Booking to UpdateBookingResponseDto")
        void mapsToUpdateResponseDto() {
            UUID id = UUID.randomUUID();
            Booking booking = createBooking(id, "Charlie", BookingStatusEnum.NO_SHOW);

            UpdateBookingResponseDto dto = bookingMapper.toUpdateBookingResponseDto(booking);

            assertNotNull(dto);
            assertEquals(id, dto.getId());
            assertEquals("Charlie", dto.getGuestName());
            assertEquals(BookingStatusEnum.NO_SHOW, dto.getStatus());
        }

        @Test
        @DisplayName("returns null when entity is null")
        void returnsNullWhenEntityIsNull() {
            assertNull(bookingMapper.toUpdateBookingResponseDto(null));
        }
    }
}
