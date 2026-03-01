package com.example.CGI_Restaurant.services.impl;

import com.example.CGI_Restaurant.domain.entities.Booking;
import com.example.CGI_Restaurant.domain.entities.BookingTable;
import com.example.CGI_Restaurant.domain.entities.TableEntity;
import com.example.CGI_Restaurant.domain.createRequests.CreateBookingTableRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateBookingTableRequest;
import com.example.CGI_Restaurant.exceptions.notFoundExceptions.BookingNotFoundException;
import com.example.CGI_Restaurant.exceptions.notFoundExceptions.BookingTableNotFoundException;
import com.example.CGI_Restaurant.exceptions.notFoundExceptions.TableEntityNotFoundException;
import com.example.CGI_Restaurant.repositories.BookingRepository;
import com.example.CGI_Restaurant.repositories.BookingTableRepository;
import com.example.CGI_Restaurant.repositories.TableEntityRepository;
import com.example.CGI_Restaurant.services.BookingTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Creates and updates booking-table links; resolves booking and table entity by ID.
 */
@Service
@RequiredArgsConstructor
public class BookingTableServiceImpl implements BookingTableService {

    private final BookingTableRepository bookingTableRepository;
    private final BookingRepository bookingRepository;
    private final TableEntityRepository tableEntityRepository;

    @Override
    public BookingTable create(CreateBookingTableRequest request) {
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new BookingNotFoundException("Booking not found"));
        TableEntity tableEntity = tableEntityRepository.findById(request.getTableEntityId())
                .orElseThrow(() -> new TableEntityNotFoundException("Table entity not found"));
        BookingTable entity = new BookingTable();
        entity.setBooking(booking);
        entity.setTableEntity(tableEntity);
        return bookingTableRepository.save(entity);
    }

    @Override
    public Page<BookingTable> list(Pageable pageable) {
        return bookingTableRepository.findAll(pageable);
    }

    @Override
    public Optional<BookingTable> getById(UUID id) {
        return bookingTableRepository.findById(id);
    }

    @Override
    @Transactional
    public BookingTable update(UUID id, UpdateBookingTableRequest request) {
        if (request.getId() == null || !id.equals(request.getId())) {
            throw new com.example.CGI_Restaurant.exceptions.updateException.BookingTableUpdateException("Booking table ID mismatch");
        }
        return bookingTableRepository.findById(id)
                .orElseThrow(() -> new BookingTableNotFoundException("Booking table not found"));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        BookingTable entity = bookingTableRepository.findById(id)
                .orElseThrow(() -> new BookingTableNotFoundException("Booking table not found"));
        bookingTableRepository.delete(entity);
    }
}
