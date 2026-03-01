package com.example.CGI_Restaurant.exceptions.notFoundExceptions;

import com.example.CGI_Restaurant.exceptions.RestaurantBookingException;

/**
 * Thrown when a zone is not found by ID. Mapped to 404 by GlobalExceptionHandler.
 */
public class ZoneNotFoundException extends RestaurantBookingException {
    public ZoneNotFoundException() {
    }

    public ZoneNotFoundException(String message) {
        super(message);
    }

    public ZoneNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ZoneNotFoundException(Throwable cause) {
        super(cause);
    }

    public ZoneNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
