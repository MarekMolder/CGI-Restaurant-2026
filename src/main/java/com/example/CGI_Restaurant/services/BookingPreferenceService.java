package com.example.CGI_Restaurant.services;

import com.example.CGI_Restaurant.domain.entities.BookingPreference;
import com.example.CGI_Restaurant.domain.createRequests.CreateBookingPreferenceRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateBookingPreferenceRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface BookingPreferenceService {
    BookingPreference create(CreateBookingPreferenceRequest request);
    Page<BookingPreference> list(Pageable pageable);
    Optional<BookingPreference> getById(UUID id);
    BookingPreference update(UUID id, UpdateBookingPreferenceRequest request);
    void delete(UUID id);
}
