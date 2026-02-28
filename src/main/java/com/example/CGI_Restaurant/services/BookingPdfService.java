package com.example.CGI_Restaurant.services;

import com.example.CGI_Restaurant.domain.entities.Booking;

/**
 * Generates a PDF document for a booking (details + QR code) to attach to confirmation email.
 */
public interface BookingPdfService {

    byte[] generateBookingPdf(Booking booking, String qrCodeImageBase64);
}
