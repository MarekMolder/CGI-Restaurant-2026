package com.example.CGI_Restaurant.services;

import com.example.CGI_Restaurant.domain.entities.Booking;
import com.example.CGI_Restaurant.domain.requests.CreateBookingRequest;

public interface BookingService {
    Booking createBooking(CreateBookingRequest booking);
}
