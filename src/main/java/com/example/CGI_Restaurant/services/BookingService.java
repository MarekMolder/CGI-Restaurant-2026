package com.example.CGI_Restaurant.services;

import com.example.CGI_Restaurant.domain.entities.Booking;
import com.example.CGI_Restaurant.domain.createRequests.CreateBookingRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateBookingRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

/**
 * Service for managing restaurant bookings. Handles creation (with slot validation, table availability,
 * preferences, QR code and email), listing by customer or admin, get/update/delete with ownership checks.
 */
public interface BookingService {

    /** Creates a booking; validates opening hours, duration, table availability and adjacency. */
    Booking createBooking(CreateBookingRequest booking);

    /** Lists bookings for a given customer (paginated). */
    Page<Booking> listBookingForCustomer(UUID customerId, Pageable pageable);

    /** Lists all bookings for admin (paginated). */
    Page<Booking> listBookingsForAdmin(Pageable pageable);

    /** Returns a booking by ID if it belongs to the customer. */
    Optional<Booking> getBookingForCustomer(UUID id, UUID customerId);

    /** Returns a booking by ID (admin or internal use). */
    Optional<Booking> getBooking(UUID id);

    /** Updates a booking; customer may only update their own. */
    Booking updateBookingForCustomer(UUID id, UUID customerId, UpdateBookingRequest booking);

    /** Updates a booking (admin). */
    Booking updateBooking(UUID id, UpdateBookingRequest booking);

    /** Deletes a booking by ID (admin). */
    void deleteBooking(UUID id);

    /** Deletes a booking if it belongs to the customer. */
    void deleteBookingForCustomer(UUID id, UUID customerId);
}
