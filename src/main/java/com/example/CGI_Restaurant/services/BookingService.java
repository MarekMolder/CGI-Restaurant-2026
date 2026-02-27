package com.example.CGI_Restaurant.services;

import com.example.CGI_Restaurant.domain.entities.Booking;
import com.example.CGI_Restaurant.domain.createRequests.CreateBookingRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateBookingRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface BookingService {
    Booking createBooking(CreateBookingRequest booking);

    Page<Booking> listBookingForCustomer(UUID customerId, Pageable pageable);
    Page<Booking> listBookingsForAdmin(Pageable pageable);

    Optional<Booking> getBookingForCustomer(UUID id, UUID customerId);
    Optional<Booking> getBooking(UUID id);

    Booking updateBookingForCustomer(UUID id, UUID customerId, UpdateBookingRequest booking);
    Booking updateBooking(UUID id, UpdateBookingRequest booking);

    void deleteBooking(UUID id);
    void deleteBookingForCustomer(UUID id, UUID customerId);

}
