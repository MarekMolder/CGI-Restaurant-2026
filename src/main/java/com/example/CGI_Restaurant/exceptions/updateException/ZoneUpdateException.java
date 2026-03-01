package com.example.CGI_Restaurant.exceptions.updateException;

import com.example.CGI_Restaurant.exceptions.RestaurantBookingException;

/**
 * Thrown when zone update validation fails (e.g. null ID or ID mismatch). Mapped to 400 by GlobalExceptionHandler.
 */
public class ZoneUpdateException extends RestaurantBookingException {
    public ZoneUpdateException() {
    }

    public ZoneUpdateException(String message) {
        super(message);
    }

    public ZoneUpdateException(String message, Throwable cause) {
        super(message, cause);
    }

    public ZoneUpdateException(Throwable cause) {
        super(cause);
    }

    public ZoneUpdateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
