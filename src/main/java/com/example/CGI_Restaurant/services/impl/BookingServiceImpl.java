package com.example.CGI_Restaurant.services.impl;

import com.example.CGI_Restaurant.domain.entities.Booking;
import com.example.CGI_Restaurant.domain.entities.BookingPreference;
import com.example.CGI_Restaurant.domain.entities.BookingTable;
import com.example.CGI_Restaurant.domain.requests.CreateBookingRequest;
import com.example.CGI_Restaurant.repositories.BookingRepository;
import com.example.CGI_Restaurant.repositories.UserRepository;
import com.example.CGI_Restaurant.services.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
