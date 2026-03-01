package com.example.CGI_Restaurant.services;

import com.example.CGI_Restaurant.domain.entities.Booking;

/**
 * Sends emails (e.g. booking confirmation with QR code) to guests.
 */
public interface EmailService {

    /** Sends a booking confirmation email to the guest, including the QR code image. */
    void sendBookingConfirmation(Booking booking, String qrCodeImageBase64);
}
