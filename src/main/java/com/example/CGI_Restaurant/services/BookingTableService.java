package com.example.CGI_Restaurant.services;

import com.example.CGI_Restaurant.domain.entities.BookingTable;
import com.example.CGI_Restaurant.domain.createRequests.CreateBookingTableRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateBookingTableRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

/**
 * CRUD service for booking-table associations (which table is assigned to a booking).
 */
public interface BookingTableService {

    BookingTable create(CreateBookingTableRequest request);
    Page<BookingTable> list(Pageable pageable);
    Optional<BookingTable> getById(UUID id);
    BookingTable update(UUID id, UpdateBookingTableRequest request);
    void delete(UUID id);
}
