package com.example.CGI_Restaurant.services;

import com.example.CGI_Restaurant.domain.entities.Booking;

public interface EmailService {

    void sendBookingConfirmation(Booking booking, String qrCodeImageBase64);
}
