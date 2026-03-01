package com.example.CGI_Restaurant.services;

import com.example.CGI_Restaurant.domain.entities.Booking;
import com.example.CGI_Restaurant.domain.entities.QrCode;

/** Service for generating and managing QR codes linked to bookings. */
public interface QrCodeService {

    /** Generates a new QR code for the booking, persists it and returns the entity. */
    QrCode generateQrCode(Booking booking);

    /** Marks the QR code as EXPIRED if the booking end time has passed. */
    QrCode markExpiredIfBookingEnded(QrCode qrCode);
}
