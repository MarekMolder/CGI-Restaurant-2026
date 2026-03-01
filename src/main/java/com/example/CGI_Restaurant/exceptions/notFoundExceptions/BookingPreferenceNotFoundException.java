package com.example.CGI_Restaurant.exceptions.notFoundExceptions;

import com.example.CGI_Restaurant.exceptions.RestaurantBookingException;

/**
 * Thrown when a booking preference is not found by ID. Mapped to 404 by GlobalExceptionHandler.
 */
public class BookingPreferenceNotFoundException extends RestaurantBookingException {
    public BookingPreferenceNotFoundException() {
    }

    public BookingPreferenceNotFoundException(String message) {
        super(message);
    }

    public BookingPreferenceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public BookingPreferenceNotFoundException(Throwable cause) {
        super(cause);
    }

    public BookingPreferenceNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
