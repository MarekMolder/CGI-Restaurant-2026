package com.example.CGI_Restaurant.mappers;

/**
 * @author AI (assisted). Used my BookingMapperTest + UserMapperTest.
 */

import com.example.CGI_Restaurant.domain.dtos.createRequests.CreateBookingPreferenceRequestDto;
import com.example.CGI_Restaurant.domain.dtos.createResponses.CreateBookingPreferenceResponseDto;
import com.example.CGI_Restaurant.domain.dtos.getResponses.GetBookingPreferenceDetailsResponseDto;
import com.example.CGI_Restaurant.domain.dtos.listResponses.ListBookingPreferenceResponseDto;
import com.example.CGI_Restaurant.domain.dtos.updateRequests.UpdateBookingPreferenceRequestDto;
import com.example.CGI_Restaurant.domain.dtos.updateResponses.UpdateBookingPreferenceResponseDto;
import com.example.CGI_Restaurant.domain.entities.Booking;
import com.example.CGI_Restaurant.domain.entities.BookingPreference;
import com.example.CGI_Restaurant.domain.entities.Feature;
import com.example.CGI_Restaurant.domain.entities.PreferencePriorityEnum;
import com.example.CGI_Restaurant.domain.createRequests.CreateBookingPreferenceRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateBookingPreferenceRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Import(com.example.CGI_Restaurant.TestMailConfig.class)
class BookingPreferenceMapperTest {

    @Autowired
    private BookingPreferenceMapper bookingPreferenceMapper;

    private static BookingPreference createBookingPreference(UUID id, PreferencePriorityEnum priority) {
        LocalDateTime now = LocalDateTime.now();
        BookingPreference bp = new BookingPreference();
        bp.setId(id);
        bp.setPriority(priority);
        bp.setBooking(Booking.builder().id(UUID.randomUUID()).build());
        bp.setFeature(Feature.builder().id(UUID.randomUUID()).name("Window").build());
        bp.setCreatedAt(now);
        bp.setUpdatedAt(now);
        return bp;
    }

    @Nested
    @DisplayName("fromDto CreateBookingPreferenceRequestDto")
    class FromDtoCreate {

        @Test
        @DisplayName("maps CreateBookingPreferenceRequestDto to CreateBookingPreferenceRequest")
        void mapsCreateDtoToRequest() {
            UUID bookingId = UUID.randomUUID();
            UUID featureId = UUID.randomUUID();
            CreateBookingPreferenceRequestDto dto = new CreateBookingPreferenceRequestDto();
            dto.setBookingId(bookingId);
            dto.setFeatureId(featureId);
            dto.setPriority(PreferencePriorityEnum.HIGH);

            CreateBookingPreferenceRequest request = bookingPreferenceMapper.fromDto(dto);

            assertNotNull(request);
            assertEquals(bookingId, request.getBookingId());
            assertEquals(featureId, request.getFeatureId());
            assertEquals(PreferencePriorityEnum.HIGH, request.getPriority());
        }

        @Test
        @DisplayName("returns null when dto is null")
        void returnsNullWhenDtoIsNull() {
            assertNull(bookingPreferenceMapper.fromDto((CreateBookingPreferenceRequestDto) null));
        }
    }

    @Nested
    @DisplayName("toDto")
    class ToDto {

        @Test
        @DisplayName("maps BookingPreference to CreateBookingPreferenceResponseDto")
        void mapsToCreateResponseDto() {
            UUID id = UUID.randomUUID();
            BookingPreference bp = createBookingPreference(id, PreferencePriorityEnum.MEDIUM);

            CreateBookingPreferenceResponseDto dto = bookingPreferenceMapper.toDto(bp);

            assertNotNull(dto);
            assertEquals(id, dto.getId());
            assertEquals(PreferencePriorityEnum.MEDIUM, dto.getPriority());
        }

        @Test
        @DisplayName("returns null when entity is null")
        void returnsNullWhenEntityIsNull() {
            assertNull(bookingPreferenceMapper.toDto(null));
        }
    }

    @Nested
    @DisplayName("toListBookingPreferenceResponseDto")
    class ToListBookingPreferenceResponseDto {

        @Test
        @DisplayName("maps BookingPreference to ListBookingPreferenceResponseDto")
        void mapsToListDto() {
            UUID id = UUID.randomUUID();
            BookingPreference bp = createBookingPreference(id, PreferencePriorityEnum.LOW);

            ListBookingPreferenceResponseDto dto = bookingPreferenceMapper.toListBookingPreferenceResponseDto(bp);

            assertNotNull(dto);
            assertEquals(id, dto.getId());
            assertEquals(PreferencePriorityEnum.LOW, dto.getPriority());
        }

        @Test
        @DisplayName("returns null when entity is null")
        void returnsNullWhenEntityIsNull() {
            assertNull(bookingPreferenceMapper.toListBookingPreferenceResponseDto(null));
        }
    }

    @Nested
    @DisplayName("toGetBookingPreferenceDetailsResponseDto")
    class ToGetBookingPreferenceDetailsResponseDto {

        @Test
        @DisplayName("maps BookingPreference to GetBookingPreferenceDetailsResponseDto")
        void mapsToGetDetailsDto() {
            UUID id = UUID.randomUUID();
            BookingPreference bp = createBookingPreference(id, PreferencePriorityEnum.HIGH);

            GetBookingPreferenceDetailsResponseDto dto = bookingPreferenceMapper.toGetBookingPreferenceDetailsResponseDto(bp);

            assertNotNull(dto);
            assertEquals(id, dto.getId());
            assertEquals(PreferencePriorityEnum.HIGH, dto.getPriority());
        }

        @Test
        @DisplayName("returns null when entity is null")
        void returnsNullWhenEntityIsNull() {
            assertNull(bookingPreferenceMapper.toGetBookingPreferenceDetailsResponseDto(null));
        }
    }

    @Nested
    @DisplayName("fromDto UpdateBookingPreferenceRequestDto")
    class FromDtoUpdate {

        @Test
        @DisplayName("maps UpdateBookingPreferenceRequestDto to UpdateBookingPreferenceRequest")
        void mapsUpdateDtoToRequest() {
            UUID id = UUID.randomUUID();
            UpdateBookingPreferenceRequestDto dto = new UpdateBookingPreferenceRequestDto();
            dto.setId(id);
            dto.setPriority(PreferencePriorityEnum.LOW);

            UpdateBookingPreferenceRequest request = bookingPreferenceMapper.fromDto(dto);

            assertNotNull(request);
            assertEquals(id, request.getId());
            assertEquals(PreferencePriorityEnum.LOW, request.getPriority());
        }

        @Test
        @DisplayName("returns null when dto is null")
        void returnsNullWhenDtoIsNull() {
            assertNull(bookingPreferenceMapper.fromDto((UpdateBookingPreferenceRequestDto) null));
        }
    }

    @Nested
    @DisplayName("toUpdateBookingPreferenceResponseDto")
    class ToUpdateBookingPreferenceResponseDto {

        @Test
        @DisplayName("maps BookingPreference to UpdateBookingPreferenceResponseDto")
        void mapsToUpdateResponseDto() {
            UUID id = UUID.randomUUID();
            BookingPreference bp = createBookingPreference(id, PreferencePriorityEnum.MEDIUM);

            UpdateBookingPreferenceResponseDto dto = bookingPreferenceMapper.toUpdateBookingPreferenceResponseDto(bp);

            assertNotNull(dto);
            assertEquals(id, dto.getId());
            assertEquals(PreferencePriorityEnum.MEDIUM, dto.getPriority());
        }

        @Test
        @DisplayName("returns null when entity is null")
        void returnsNullWhenEntityIsNull() {
            assertNull(bookingPreferenceMapper.toUpdateBookingPreferenceResponseDto(null));
        }
    }
}
