package com.example.CGI_Restaurant.services.impl;

import com.example.CGI_Restaurant.domain.entities.Booking;
import com.example.CGI_Restaurant.domain.entities.BookingPreference;
import com.example.CGI_Restaurant.domain.entities.Feature;
import com.example.CGI_Restaurant.domain.createRequests.CreateBookingPreferenceRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateBookingPreferenceRequest;
import com.example.CGI_Restaurant.exceptions.notFoundExceptions.BookingNotFoundException;
import com.example.CGI_Restaurant.exceptions.notFoundExceptions.BookingPreferenceNotFoundException;
import com.example.CGI_Restaurant.exceptions.notFoundExceptions.FeatureNotFoundException;
import com.example.CGI_Restaurant.exceptions.updateException.BookingPreferenceUpdateException;
import com.example.CGI_Restaurant.repositories.BookingPreferenceRepository;
import com.example.CGI_Restaurant.repositories.BookingRepository;
import com.example.CGI_Restaurant.repositories.FeatureRepository;
import com.example.CGI_Restaurant.services.BookingPreferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingPreferenceServiceImpl implements BookingPreferenceService {

    private final BookingPreferenceRepository bookingPreferenceRepository;
    private final BookingRepository bookingRepository;
    private final FeatureRepository featureRepository;

    @Override
    public BookingPreference create(CreateBookingPreferenceRequest request) {
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new BookingNotFoundException("Booking not found"));
        Feature feature = featureRepository.findById(request.getFeatureId())
                .orElseThrow(() -> new FeatureNotFoundException("Feature not found"));
        BookingPreference entity = new BookingPreference();
        entity.setBooking(booking);
        entity.setFeature(feature);
        entity.setPriority(request.getPriority());
        return bookingPreferenceRepository.save(entity);
    }

    @Override
    public Page<BookingPreference> list(Pageable pageable) {
        return bookingPreferenceRepository.findAll(pageable);
    }

    @Override
    public Optional<BookingPreference> getById(UUID id) {
        return bookingPreferenceRepository.findById(id);
    }

    @Override
    @Transactional
    public BookingPreference update(UUID id, UpdateBookingPreferenceRequest request) {
        if (request.getId() == null || !id.equals(request.getId())) {
            throw new BookingPreferenceUpdateException("Booking preference ID mismatch");
        }
        BookingPreference entity = bookingPreferenceRepository.findById(id)
                .orElseThrow(() -> new BookingPreferenceNotFoundException("Booking preference not found"));
        entity.setPriority(request.getPriority());
        return bookingPreferenceRepository.save(entity);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        BookingPreference entity = bookingPreferenceRepository.findById(id)
                .orElseThrow(() -> new BookingPreferenceNotFoundException("Booking preference not found"));
        bookingPreferenceRepository.delete(entity);
    }
}
