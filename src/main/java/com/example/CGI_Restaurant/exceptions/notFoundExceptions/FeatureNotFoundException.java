package com.example.CGI_Restaurant.exceptions.notFoundExceptions;

import com.example.CGI_Restaurant.exceptions.RestaurantBookingException;

/**
 * Thrown when a feature is not found by ID. Mapped to 404 by GlobalExceptionHandler.
 */
public class FeatureNotFoundException extends RestaurantBookingException {
    public FeatureNotFoundException() {
    }

    public FeatureNotFoundException(String message) {
        super(message);
    }

    public FeatureNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public FeatureNotFoundException(Throwable cause) {
        super(cause);
    }

    public FeatureNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
