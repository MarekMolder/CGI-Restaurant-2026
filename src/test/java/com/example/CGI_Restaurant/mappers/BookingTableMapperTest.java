package com.example.CGI_Restaurant.mappers;

/**
 * @author AI (assisted). Used my BookingMapperTest + UserMapperTest.
 */

import com.example.CGI_Restaurant.domain.dtos.createRequests.CreateBookingTableRequestDto;
import com.example.CGI_Restaurant.domain.dtos.createResponses.CreateBookingTableResponseDto;
import com.example.CGI_Restaurant.domain.dtos.getResponses.GetBookingTableDetailsResponseDto;
import com.example.CGI_Restaurant.domain.dtos.listResponses.ListBookingTableResponseDto;
import com.example.CGI_Restaurant.domain.dtos.updateRequests.UpdateBookingTableRequestDto;
import com.example.CGI_Restaurant.domain.dtos.updateResponses.UpdateBookingTableResponseDto;
import com.example.CGI_Restaurant.domain.entities.Booking;
import com.example.CGI_Restaurant.domain.entities.BookingTable;
import com.example.CGI_Restaurant.domain.entities.TableEntity;
import com.example.CGI_Restaurant.domain.createRequests.CreateBookingTableRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateBookingTableRequest;
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
class BookingTableMapperTest {

    @Autowired
    private BookingTableMapper bookingTableMapper;

    private static BookingTable createBookingTable(UUID id) {
        LocalDateTime now = LocalDateTime.now();
        BookingTable bt = new BookingTable();
        bt.setId(id);
        bt.setBooking(Booking.builder().id(UUID.randomUUID()).build());
        bt.setTableEntity(TableEntity.builder().id(UUID.randomUUID()).label("T1").build());
        bt.setCreatedAt(now);
        bt.setUpdatedAt(now);
        return bt;
    }

    @Nested
    @DisplayName("fromDto CreateBookingTableRequestDto")
    class FromDtoCreate {

        @Test
        @DisplayName("maps CreateBookingTableRequestDto to CreateBookingTableRequest")
        void mapsCreateDtoToRequest() {
            UUID bookingId = UUID.randomUUID();
            UUID tableEntityId = UUID.randomUUID();
            CreateBookingTableRequestDto dto = new CreateBookingTableRequestDto();
            dto.setBookingId(bookingId);
            dto.setTableEntityId(tableEntityId);

            CreateBookingTableRequest request = bookingTableMapper.fromDto(dto);

            assertNotNull(request);
            assertEquals(bookingId, request.getBookingId());
            assertEquals(tableEntityId, request.getTableEntityId());
        }

        @Test
        @DisplayName("returns null when dto is null")
        void returnsNullWhenDtoIsNull() {
            assertNull(bookingTableMapper.fromDto((CreateBookingTableRequestDto) null));
        }
    }

    @Nested
    @DisplayName("toDto")
    class ToDto {

        @Test
        @DisplayName("maps BookingTable to CreateBookingTableResponseDto")
        void mapsToCreateResponseDto() {
            UUID id = UUID.randomUUID();
            BookingTable bt = createBookingTable(id);

            CreateBookingTableResponseDto dto = bookingTableMapper.toDto(bt);

            assertNotNull(dto);
            assertEquals(id, dto.getId());
        }

        @Test
        @DisplayName("returns null when entity is null")
        void returnsNullWhenEntityIsNull() {
            assertNull(bookingTableMapper.toDto(null));
        }
    }

    @Nested
    @DisplayName("toListBookingTableResponseDto")
    class ToListBookingTableResponseDto {

        @Test
        @DisplayName("maps BookingTable to ListBookingTableResponseDto")
        void mapsToListDto() {
            UUID id = UUID.randomUUID();
            BookingTable bt = createBookingTable(id);

            ListBookingTableResponseDto dto = bookingTableMapper.toListBookingTableResponseDto(bt);

            assertNotNull(dto);
            assertEquals(id, dto.getId());
        }

        @Test
        @DisplayName("returns null when entity is null")
        void returnsNullWhenEntityIsNull() {
            assertNull(bookingTableMapper.toListBookingTableResponseDto(null));
        }
    }

    @Nested
    @DisplayName("toGetBookingTableDetailsResponseDto")
    class ToGetBookingTableDetailsResponseDto {

        @Test
        @DisplayName("maps BookingTable to GetBookingTableDetailsResponseDto")
        void mapsToGetDetailsDto() {
            UUID id = UUID.randomUUID();
            BookingTable bt = createBookingTable(id);

            GetBookingTableDetailsResponseDto dto = bookingTableMapper.toGetBookingTableDetailsResponseDto(bt);

            assertNotNull(dto);
            assertEquals(id, dto.getId());
        }

        @Test
        @DisplayName("returns null when entity is null")
        void returnsNullWhenEntityIsNull() {
            assertNull(bookingTableMapper.toGetBookingTableDetailsResponseDto(null));
        }
    }

    @Nested
    @DisplayName("fromDto UpdateBookingTableRequestDto")
    class FromDtoUpdate {

        @Test
        @DisplayName("maps UpdateBookingTableRequestDto to UpdateBookingTableRequest")
        void mapsUpdateDtoToRequest() {
            UUID id = UUID.randomUUID();
            UpdateBookingTableRequestDto dto = new UpdateBookingTableRequestDto();
            dto.setId(id);

            UpdateBookingTableRequest request = bookingTableMapper.fromDto(dto);

            assertNotNull(request);
            assertEquals(id, request.getId());
        }

        @Test
        @DisplayName("returns null when dto is null")
        void returnsNullWhenDtoIsNull() {
            assertNull(bookingTableMapper.fromDto((UpdateBookingTableRequestDto) null));
        }
    }

    @Nested
    @DisplayName("toUpdateBookingTableResponseDto")
    class ToUpdateBookingTableResponseDto {

        @Test
        @DisplayName("maps BookingTable to UpdateBookingTableResponseDto")
        void mapsToUpdateResponseDto() {
            UUID id = UUID.randomUUID();
            BookingTable bt = createBookingTable(id);

            UpdateBookingTableResponseDto dto = bookingTableMapper.toUpdateBookingTableResponseDto(bt);

            assertNotNull(dto);
            assertEquals(id, dto.getId());
        }

        @Test
        @DisplayName("returns null when entity is null")
        void returnsNullWhenEntityIsNull() {
            assertNull(bookingTableMapper.toUpdateBookingTableResponseDto(null));
        }
    }
}
