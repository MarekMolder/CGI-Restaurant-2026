package com.example.CGI_Restaurant.exceptions.updateException;

import com.example.CGI_Restaurant.exceptions.RestaurantBookingException;

/**
 * Thrown when seating plan update validation fails (e.g. null ID or ID mismatch). Mapped to 400 by GlobalExceptionHandler.
 */
public class SeatingPlanUpdateException extends RestaurantBookingException {
    public SeatingPlanUpdateException() {
    }

    public SeatingPlanUpdateException(String message) {
        super(message);
    }

    public SeatingPlanUpdateException(String message, Throwable cause) {
        super(message, cause);
    }

    public SeatingPlanUpdateException(Throwable cause) {
        super(cause);
    }

    public SeatingPlanUpdateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
