package com.example.CGI_Restaurant.exceptions.updateException;

import com.example.CGI_Restaurant.exceptions.RestaurantBookingException;

/**
 * Thrown when feature update validation fails (e.g. null ID or ID mismatch). Mapped to 400 by GlobalExceptionHandler.
 */
public class FeatureUpdateException extends RestaurantBookingException {
    public FeatureUpdateException() {
    }

    public FeatureUpdateException(String message) {
        super(message);
    }

    public FeatureUpdateException(String message, Throwable cause) {
        super(message, cause);
    }

    public FeatureUpdateException(Throwable cause) {
        super(cause);
    }

    public FeatureUpdateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
