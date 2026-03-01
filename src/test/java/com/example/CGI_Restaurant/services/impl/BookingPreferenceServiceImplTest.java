package com.example.CGI_Restaurant.services.impl;

/**
 * @author AI (assisted). Used my BookingSeviceImplTest.
 */

import com.example.CGI_Restaurant.domain.entities.Booking;
import com.example.CGI_Restaurant.domain.entities.BookingPreference;
import com.example.CGI_Restaurant.domain.entities.Feature;
import com.example.CGI_Restaurant.domain.entities.PreferencePriorityEnum;
import com.example.CGI_Restaurant.domain.createRequests.CreateBookingPreferenceRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateBookingPreferenceRequest;
import com.example.CGI_Restaurant.exceptions.notFoundExceptions.BookingNotFoundException;
import com.example.CGI_Restaurant.exceptions.notFoundExceptions.BookingPreferenceNotFoundException;
import com.example.CGI_Restaurant.exceptions.notFoundExceptions.FeatureNotFoundException;
import com.example.CGI_Restaurant.exceptions.updateException.BookingPreferenceUpdateException;
import com.example.CGI_Restaurant.repositories.BookingPreferenceRepository;
import com.example.CGI_Restaurant.repositories.BookingRepository;
import com.example.CGI_Restaurant.repositories.FeatureRepository;
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
class BookingPreferenceServiceImplTest {

    @Mock
    private BookingPreferenceRepository bookingPreferenceRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private FeatureRepository featureRepository;

    @InjectMocks
    private BookingPreferenceServiceImpl service;

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("saves new preference when booking and feature exist")
        void createsWhenBothExist() {
            UUID bookingId = UUID.randomUUID();
            UUID featureId = UUID.randomUUID();
            Booking booking = Booking.builder().id(bookingId).build();
            Feature feature = Feature.builder().id(featureId).name("Aknaäärne").build();

            CreateBookingPreferenceRequest request = new CreateBookingPreferenceRequest();
            request.setBookingId(bookingId);
            request.setFeatureId(featureId);
            request.setPriority(PreferencePriorityEnum.HIGH);

            BookingPreference saved = BookingPreference.builder().id(UUID.randomUUID()).booking(booking).feature(feature).priority(PreferencePriorityEnum.HIGH).build();
            when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
            when(featureRepository.findById(featureId)).thenReturn(Optional.of(feature));
            when(bookingPreferenceRepository.save(any(BookingPreference.class))).thenReturn(saved);

            BookingPreference result = service.create(request);

            assertNotNull(result);
            assertEquals(PreferencePriorityEnum.HIGH, result.getPriority());
        }

        @Test
        @DisplayName("throws BookingNotFoundException when booking not found")
        void throwsWhenBookingNotFound() {
            UUID bookingId = UUID.randomUUID();
            CreateBookingPreferenceRequest request = new CreateBookingPreferenceRequest();
            request.setBookingId(bookingId);
            request.setFeatureId(UUID.randomUUID());
            request.setPriority(PreferencePriorityEnum.MEDIUM);
            when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

            assertThrows(BookingNotFoundException.class, () -> service.create(request));
            verify(bookingPreferenceRepository, never()).save(any());
        }

        @Test
        @DisplayName("throws FeatureNotFoundException when feature not found")
        void throwsWhenFeatureNotFound() {
            UUID bookingId = UUID.randomUUID();
            UUID featureId = UUID.randomUUID();
            when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(Booking.builder().id(bookingId).build()));
            when(featureRepository.findById(featureId)).thenReturn(Optional.empty());
            CreateBookingPreferenceRequest request = new CreateBookingPreferenceRequest();
            request.setBookingId(bookingId);
            request.setFeatureId(featureId);
            request.setPriority(PreferencePriorityEnum.LOW);

            assertThrows(FeatureNotFoundException.class, () -> service.create(request));
            verify(bookingPreferenceRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("list")
    class ListMethod {

        @Test
        @DisplayName("returns page from repository")
        void returnsPage() {
            Pageable pageable = PageRequest.of(0, 10);
            BookingPreference bp = BookingPreference.builder().id(UUID.randomUUID()).priority(PreferencePriorityEnum.MEDIUM).build();
            when(bookingPreferenceRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(bp), pageable, 1));
            Page<BookingPreference> result = service.list(pageable);
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
            BookingPreference bp = BookingPreference.builder().id(id).priority(PreferencePriorityEnum.HIGH).build();
            when(bookingPreferenceRepository.findById(id)).thenReturn(Optional.of(bp));
            assertTrue(service.getById(id).isPresent());
        }

        @Test
        @DisplayName("returns empty when not found")
        void returnsEmptyWhenNotFound() {
            when(bookingPreferenceRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
            assertTrue(service.getById(UUID.randomUUID()).isEmpty());
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("updates priority and saves when id matches")
        void updatesWhenIdMatches() {
            UUID id = UUID.randomUUID();
            UpdateBookingPreferenceRequest request = new UpdateBookingPreferenceRequest();
            request.setId(id);
            request.setPriority(PreferencePriorityEnum.LOW);

            BookingPreference existing = BookingPreference.builder().id(id).priority(PreferencePriorityEnum.HIGH).build();
            when(bookingPreferenceRepository.findById(id)).thenReturn(Optional.of(existing));
            when(bookingPreferenceRepository.save(any(BookingPreference.class))).thenAnswer(inv -> inv.getArgument(0));

            BookingPreference result = service.update(id, request);

            assertEquals(PreferencePriorityEnum.LOW, result.getPriority());
            verify(bookingPreferenceRepository).save(existing);
        }

        @Test
        @DisplayName("throws BookingPreferenceUpdateException when request id is null")
        void throwsWhenRequestIdNull() {
            UpdateBookingPreferenceRequest request = new UpdateBookingPreferenceRequest();
            request.setId(null);
            request.setPriority(PreferencePriorityEnum.MEDIUM);
            assertThrows(BookingPreferenceUpdateException.class, () -> service.update(UUID.randomUUID(), request));
            verify(bookingPreferenceRepository, never()).save(any());
        }

        @Test
        @DisplayName("throws BookingPreferenceUpdateException when path id and request id differ")
        void throwsWhenIdMismatch() {
            UpdateBookingPreferenceRequest request = new UpdateBookingPreferenceRequest();
            request.setId(UUID.randomUUID());
            request.setPriority(PreferencePriorityEnum.MEDIUM);
            assertThrows(BookingPreferenceUpdateException.class, () -> service.update(UUID.randomUUID(), request));
        }

        @Test
        @DisplayName("throws BookingPreferenceNotFoundException when not found")
        void throwsWhenNotFound() {
            UUID id = UUID.randomUUID();
            UpdateBookingPreferenceRequest request = new UpdateBookingPreferenceRequest();
            request.setId(id);
            request.setPriority(PreferencePriorityEnum.HIGH);
            when(bookingPreferenceRepository.findById(id)).thenReturn(Optional.empty());
            assertThrows(BookingPreferenceNotFoundException.class, () -> service.update(id, request));
            verify(bookingPreferenceRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("deletes when exists")
        void deletesWhenExists() {
            UUID id = UUID.randomUUID();
            BookingPreference bp = BookingPreference.builder().id(id).build();
            when(bookingPreferenceRepository.findById(id)).thenReturn(Optional.of(bp));
            service.delete(id);
            verify(bookingPreferenceRepository).delete(bp);
        }

        @Test
        @DisplayName("throws BookingPreferenceNotFoundException when not found")
        void throwsWhenNotFound() {
            UUID id = UUID.randomUUID();
            when(bookingPreferenceRepository.findById(id)).thenReturn(Optional.empty());
            assertThrows(BookingPreferenceNotFoundException.class, () -> service.delete(id));
            verify(bookingPreferenceRepository, never()).delete(any());
        }
    }
}
