package com.example.CGI_Restaurant.services.impl;

import com.example.CGI_Restaurant.domain.entities.Booking;
import com.example.CGI_Restaurant.domain.entities.BookingPreference;
import com.example.CGI_Restaurant.domain.entities.BookingTable;
import com.example.CGI_Restaurant.domain.createRequests.CreateBookingRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateBookingPreferenceRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateBookingRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateBookingTableRequest;
import com.example.CGI_Restaurant.exceptions.notFoundExceptions.BookingNotFoundException;
import com.example.CGI_Restaurant.exceptions.notFoundExceptions.BookingPreferenceNotFoundException;
import com.example.CGI_Restaurant.exceptions.updateException.BookingPreferenceUpdateException;
import com.example.CGI_Restaurant.exceptions.updateException.BookingUpdateException;
import com.example.CGI_Restaurant.repositories.BookingRepository;
import com.example.CGI_Restaurant.repositories.UserRepository;
import com.example.CGI_Restaurant.services.BookingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    public Booking createBooking(CreateBookingRequest booking) {

        List<BookingPreference> bookingPreferencesToCreate = booking.getBookingPreferences().stream().map(
                preference -> {
                    BookingPreference bookingPreferenceToCreate = new BookingPreference();
                    bookingPreferenceToCreate.setPriority(preference.getPriority());
                    return bookingPreferenceToCreate;
                }).toList();

        List<BookingTable> bookingTablesToCreate = booking.getBookingTableRequests().stream().map(
                bookingTable -> {
                    BookingTable bookingTableToCreate = new BookingTable();
                    return bookingTableToCreate;
                }).toList();

        Booking bookingToCreate = new Booking();
        bookingToCreate.setGuestName(booking.getUser().getName());
        bookingToCreate.setGuestEmail(booking.getUser().getEmail());
        bookingToCreate.setStartAt(booking.getStartAt());
        bookingToCreate.setEndAt(booking.getEndAt());
        bookingToCreate.setPartySize(booking.getPartySize());
        bookingToCreate.setStatus(booking.getStatus());
        bookingToCreate.setQrToken(booking.getQrToken());
        bookingToCreate.setSpecialRequests(booking.getSpecialRequests());
        bookingToCreate.setUser(booking.getUser());
        bookingToCreate.setBookingPreferences(bookingPreferencesToCreate);
        bookingToCreate.setBookingTables(bookingTablesToCreate);

        return bookingRepository.save(bookingToCreate);
    }

    @Override
    public Page<Booking> listBookingForCustomer(UUID customerId, Pageable pageable) {
        return bookingRepository.findByUserId(customerId, pageable);
    }

    @Override
    public Page<Booking> listBookingsForAdmin(Pageable pageable) {
        return bookingRepository.findAll(pageable);
    }

    @Override
    public Optional<Booking> getBookingForCustomer(UUID id, UUID customerId) {
        return bookingRepository.findByIdAndCustomerId(id, customerId);
    }

    @Override
    public Optional<Booking> getBooking(UUID id) {
        return bookingRepository.findById(id);
    }

    @Override
    @Transactional
    public Booking updateBookingForCustomer(UUID id, UUID customerId, UpdateBookingRequest booking) {
        if(booking.getId() == null) {
            throw new BookingUpdateException("Booking ID cannot be null");
        }

        if(!id.equals(booking.getId())) {
            throw new BookingUpdateException("Cannot update the ID of a booking");
        }

        Booking existingBooking = bookingRepository
                .findByIdAndCustomerId(id, customerId)
                .orElseThrow(() -> new BookingNotFoundException(
                        String.format("Booking with ID '%s' does not exist", id))
                );

        existingBooking.setGuestName(booking.getGuestName());
        existingBooking.setGuestEmail(booking.getGuestEmail());
        existingBooking.setStartAt(booking.getStartAt());
        existingBooking.setEndAt(booking.getEndAt());
        existingBooking.setPartySize(booking.getPartySize());
        existingBooking.setStatus(booking.getStatus());
        existingBooking.setQrToken(booking.getQrToken());
        existingBooking.setSpecialRequests(booking.getSpecialRequests());

        Set<UUID> requestBookingPreferenceIds = booking.getBookingPreferences()
                .stream()
                .map(UpdateBookingPreferenceRequest::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        existingBooking.getBookingPreferences().removeIf(existingBookingPreference ->
                !requestBookingPreferenceIds.contains(existingBookingPreference.getId()));

        Map<UUID, BookingPreference> existingBookingPreferenceIndex = existingBooking.getBookingPreferences().stream()
                        .collect(Collectors.toMap(BookingPreference::getId, Function.identity()));

        for(UpdateBookingPreferenceRequest bookingPreference : booking.getBookingPreferences()) {
            if(null == bookingPreference.getId()) {
                BookingPreference bookingPreferenceToCreate = new BookingPreference();
                bookingPreferenceToCreate.setPriority(bookingPreference.getPriority());
                existingBooking.getBookingPreferences().add(bookingPreferenceToCreate);
            } else if (existingBookingPreferenceIndex.containsKey(bookingPreference.getId())) {
                BookingPreference existingBookingPreference = existingBookingPreferenceIndex.get(bookingPreference.getId());
                existingBookingPreference.setPriority(bookingPreference.getPriority());
            } else {
                throw new BookingPreferenceNotFoundException(String.format("" +
                        "Booking preference with ID '%s' does not exist", bookingPreference.getId()));
            }
        }

        return bookingRepository.save(existingBooking);
    }

    @Override
    @Transactional
    public Booking updateBooking(UUID id, UpdateBookingRequest booking) {
        if(booking.getId() == null) {
            throw new BookingUpdateException("Booking ID cannot be null");
        }

        if(!id.equals(booking.getId())) {
            throw new BookingUpdateException("Cannot update the ID of a booking");
        }

        Booking existingBooking = bookingRepository
                .findById(id)
                .orElseThrow(() -> new BookingNotFoundException(
                        String.format("Booking with ID '%s' does not exist", id))
                );

        existingBooking.setGuestName(booking.getGuestName());
        existingBooking.setGuestEmail(booking.getGuestEmail());
        existingBooking.setStartAt(booking.getStartAt());
        existingBooking.setEndAt(booking.getEndAt());
        existingBooking.setPartySize(booking.getPartySize());
        existingBooking.setStatus(booking.getStatus());
        existingBooking.setQrToken(booking.getQrToken());
        existingBooking.setSpecialRequests(booking.getSpecialRequests());

        Set<UUID> requestBookingPreferenceIds = booking.getBookingPreferences()
                .stream()
                .map(UpdateBookingPreferenceRequest::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        existingBooking.getBookingPreferences().removeIf(existingBookingPreference ->
                !requestBookingPreferenceIds.contains(existingBookingPreference.getId()));

        Map<UUID, BookingPreference> existingBookingPreferenceIndex = existingBooking.getBookingPreferences().stream()
                .collect(Collectors.toMap(BookingPreference::getId, Function.identity()));

        for(UpdateBookingPreferenceRequest bookingPreference : booking.getBookingPreferences()) {
            if(null == bookingPreference.getId()) {
                BookingPreference bookingPreferenceToCreate = new BookingPreference();
                bookingPreferenceToCreate.setPriority(bookingPreference.getPriority());
                existingBooking.getBookingPreferences().add(bookingPreferenceToCreate);
            } else if (existingBookingPreferenceIndex.containsKey(bookingPreference.getId())) {
                BookingPreference existingBookingPreference = existingBookingPreferenceIndex.get(bookingPreference.getId());
                existingBookingPreference.setPriority(bookingPreference.getPriority());
            } else {
                throw new BookingPreferenceNotFoundException(String.format("" +
                        "Booking preference with ID '%s' does not exist", bookingPreference.getId()));
            }
        }

        return bookingRepository.save(existingBooking);
    }

    @Override
    @Transactional
    public void deleteBooking(UUID id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(
                        String.format("Booking with ID '%s' does not exist", id)));
        bookingRepository.delete(booking);
    }

    @Override
    @Transactional
    public void deleteBookingForCustomer(UUID id, UUID customerId) {
        Booking booking = bookingRepository.findByIdAndCustomerId(id, customerId)
                .orElseThrow(() -> new BookingNotFoundException(
                        String.format("Booking with ID '%s' does not exist or does not belong to you", id)));
        bookingRepository.delete(booking);
    }
}
