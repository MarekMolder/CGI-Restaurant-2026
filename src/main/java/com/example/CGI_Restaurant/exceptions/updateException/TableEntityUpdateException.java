package com.example.CGI_Restaurant.exceptions.updateException;

import com.example.CGI_Restaurant.exceptions.RestaurantBookingException;

/**
 * Thrown when table entity update validation fails (e.g. null ID or ID mismatch). Mapped to 400 by GlobalExceptionHandler.
 */
public class TableEntityUpdateException extends RestaurantBookingException {
    public TableEntityUpdateException() {
    }

    public TableEntityUpdateException(String message) {
        super(message);
    }

    public TableEntityUpdateException(String message, Throwable cause) {
        super(message, cause);
    }

    public TableEntityUpdateException(Throwable cause) {
        super(cause);
    }

    public TableEntityUpdateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
