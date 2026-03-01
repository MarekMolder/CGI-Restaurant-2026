package com.example.CGI_Restaurant.exceptions.notFoundExceptions;

import com.example.CGI_Restaurant.exceptions.RestaurantBookingException;

/**
 * Thrown when a seating plan is not found by ID. Mapped to 404 by GlobalExceptionHandler.
 */
public class SeatingPlanNotFoundException extends RestaurantBookingException {
    public SeatingPlanNotFoundException() {
    }

    public SeatingPlanNotFoundException(String message) {
        super(message);
    }

    public SeatingPlanNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public SeatingPlanNotFoundException(Throwable cause) {
        super(cause);
    }

    public SeatingPlanNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
