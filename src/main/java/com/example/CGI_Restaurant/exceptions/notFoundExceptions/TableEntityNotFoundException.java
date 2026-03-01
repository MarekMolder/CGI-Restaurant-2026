package com.example.CGI_Restaurant.exceptions.notFoundExceptions;

import com.example.CGI_Restaurant.exceptions.RestaurantBookingException;

/**
 * Thrown when a table entity is not found by ID. Mapped to 404 by GlobalExceptionHandler.
 */
public class TableEntityNotFoundException extends RestaurantBookingException {
    public TableEntityNotFoundException() {
    }

    public TableEntityNotFoundException(String message) {
        super(message);
    }

    public TableEntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public TableEntityNotFoundException(Throwable cause) {
        super(cause);
    }

    public TableEntityNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
