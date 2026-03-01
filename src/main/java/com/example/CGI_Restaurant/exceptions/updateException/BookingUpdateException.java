package com.example.CGI_Restaurant.exceptions.updateException;

import com.example.CGI_Restaurant.exceptions.RestaurantBookingException;

/**
 * Thrown when booking update validation fails (e.g. null ID or ID mismatch). Mapped to 400 by GlobalExceptionHandler.
 */
public class BookingUpdateException extends RestaurantBookingException {
    public BookingUpdateException() {
    }

    public BookingUpdateException(String message) {
        super(message);
    }

    public BookingUpdateException(String message, Throwable cause) {
        super(message, cause);
    }

    public BookingUpdateException(Throwable cause) {
        super(cause);
    }

    public BookingUpdateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
