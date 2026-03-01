package com.example.CGI_Restaurant.services.impl;

/**
 * @author AI (assisted). Used my BookingSeviceImplTest.
 */

import com.example.CGI_Restaurant.domain.entities.Booking;
import com.example.CGI_Restaurant.domain.entities.BookingTable;
import com.example.CGI_Restaurant.domain.entities.TableEntity;
import com.example.CGI_Restaurant.domain.createRequests.CreateBookingTableRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateBookingTableRequest;
import com.example.CGI_Restaurant.exceptions.notFoundExceptions.BookingNotFoundException;
import com.example.CGI_Restaurant.exceptions.notFoundExceptions.BookingTableNotFoundException;
import com.example.CGI_Restaurant.exceptions.notFoundExceptions.TableEntityNotFoundException;
import com.example.CGI_Restaurant.exceptions.updateException.BookingTableUpdateException;
import com.example.CGI_Restaurant.repositories.BookingRepository;
import com.example.CGI_Restaurant.repositories.BookingTableRepository;
import com.example.CGI_Restaurant.repositories.TableEntityRepository;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingTableServiceImplTest {

    @Mock
    private BookingTableRepository bookingTableRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private TableEntityRepository tableEntityRepository;

    @InjectMocks
    private BookingTableServiceImpl service;

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("saves new booking table when booking and table exist")
        void createsWhenBothExist() {
            UUID bookingId = UUID.randomUUID();
            UUID tableId = UUID.randomUUID();
            Booking booking = Booking.builder().id(bookingId).build();
            TableEntity table = TableEntity.builder().id(tableId).label("Laud 1").build();

            CreateBookingTableRequest request = new CreateBookingTableRequest();
            request.setBookingId(bookingId);
            request.setTableEntityId(tableId);

            BookingTable saved = new BookingTable();
            saved.setId(UUID.randomUUID());
            saved.setBooking(booking);
            saved.setTableEntity(table);
            when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
            when(tableEntityRepository.findById(tableId)).thenReturn(Optional.of(table));
            when(bookingTableRepository.save(any(BookingTable.class))).thenReturn(saved);

            BookingTable result = service.create(request);

            assertNotNull(result);
            assertEquals(bookingId, result.getBooking().getId());
            assertEquals(tableId, result.getTableEntity().getId());
        }

        @Test
        @DisplayName("throws BookingNotFoundException when booking not found")
        void throwsWhenBookingNotFound() {
            UUID bookingId = UUID.randomUUID();
            CreateBookingTableRequest request = new CreateBookingTableRequest();
            request.setBookingId(bookingId);
            request.setTableEntityId(UUID.randomUUID());
            when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

            assertThrows(BookingNotFoundException.class, () -> service.create(request));
            verify(bookingTableRepository, never()).save(any());
        }

        @Test
        @DisplayName("throws TableEntityNotFoundException when table not found")
        void throwsWhenTableNotFound() {
            UUID bookingId = UUID.randomUUID();
            UUID tableId = UUID.randomUUID();
            when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(Booking.builder().id(bookingId).build()));
            when(tableEntityRepository.findById(tableId)).thenReturn(Optional.empty());
            CreateBookingTableRequest request = new CreateBookingTableRequest();
            request.setBookingId(bookingId);
            request.setTableEntityId(tableId);

            assertThrows(TableEntityNotFoundException.class, () -> service.create(request));
            verify(bookingTableRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("list")
    class ListMethod {

        @Test
        @DisplayName("returns page from repository")
        void returnsPage() {
            Pageable pageable = PageRequest.of(0, 10);
            BookingTable bt = new BookingTable();
            bt.setId(UUID.randomUUID());
            when(bookingTableRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(bt), pageable, 1));
            Page<BookingTable> result = service.list(pageable);
            assertEquals(1, result.getContent().size());
        }
    }

    @Nested
    @DisplayName("getById")
    class GetById {

        @Test
        @DisplayName("returns present when found")
        void returnsPresentWhenFound() {
            UUID id = UUID.randomUUID();
            BookingTable bt = new BookingTable();
            bt.setId(id);
            when(bookingTableRepository.findById(id)).thenReturn(Optional.of(bt));
            assertTrue(service.getById(id).isPresent());
        }

        @Test
        @DisplayName("returns empty when not found")
        void returnsEmptyWhenNotFound() {
            when(bookingTableRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
            assertTrue(service.getById(UUID.randomUUID()).isEmpty());
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("returns existing entity when id matches (no field update applied)")
        void returnsEntityWhenIdMatches() {
            UUID id = UUID.randomUUID();
            UpdateBookingTableRequest request = new UpdateBookingTableRequest();
            request.setId(id);
            BookingTable existing = new BookingTable();
            existing.setId(id);
            when(bookingTableRepository.findById(id)).thenReturn(Optional.of(existing));

            BookingTable result = service.update(id, request);

            assertEquals(id, result.getId());
            verify(bookingTableRepository, never()).save(any());
        }

        @Test
        @DisplayName("throws BookingTableUpdateException when request id is null")
        void throwsWhenRequestIdNull() {
            UpdateBookingTableRequest request = new UpdateBookingTableRequest();
            request.setId(null);
            assertThrows(BookingTableUpdateException.class, () -> service.update(UUID.randomUUID(), request));
            verify(bookingTableRepository, never()).save(any());
        }

        @Test
        @DisplayName("throws BookingTableUpdateException when path id and request id differ")
        void throwsWhenIdMismatch() {
            UpdateBookingTableRequest request = new UpdateBookingTableRequest();
            request.setId(UUID.randomUUID());
            assertThrows(BookingTableUpdateException.class, () -> service.update(UUID.randomUUID(), request));
        }

        @Test
        @DisplayName("throws BookingTableNotFoundException when not found")
        void throwsWhenNotFound() {
            UUID id = UUID.randomUUID();
            UpdateBookingTableRequest request = new UpdateBookingTableRequest();
            request.setId(id);
            when(bookingTableRepository.findById(id)).thenReturn(Optional.empty());
            assertThrows(BookingTableNotFoundException.class, () -> service.update(id, request));
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("deletes when exists")
        void deletesWhenExists() {
            UUID id = UUID.randomUUID();
            BookingTable bt = new BookingTable();
            bt.setId(id);
            when(bookingTableRepository.findById(id)).thenReturn(Optional.of(bt));
            service.delete(id);
            verify(bookingTableRepository).delete(bt);
        }

        @Test
        @DisplayName("throws BookingTableNotFoundException when not found")
        void throwsWhenNotFound() {
            UUID id = UUID.randomUUID();
            when(bookingTableRepository.findById(id)).thenReturn(Optional.empty());
            assertThrows(BookingTableNotFoundException.class, () -> service.delete(id));
            verify(bookingTableRepository, never()).delete(any());
        }
    }
}
