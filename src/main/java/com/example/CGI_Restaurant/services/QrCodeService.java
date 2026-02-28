package com.example.CGI_Restaurant.services;

import com.example.CGI_Restaurant.domain.entities.Booking;
import com.example.CGI_Restaurant.domain.entities.QrCode;

public interface QrCodeService {
    QrCode generateQrCode(Booking booking);
}
