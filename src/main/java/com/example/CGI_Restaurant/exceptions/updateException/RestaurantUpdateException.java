package com.example.CGI_Restaurant.exceptions.updateException;

import com.example.CGI_Restaurant.exceptions.RestaurantBookingException;

/**
 * Thrown when restaurant update validation fails (e.g. null ID or ID mismatch). Mapped to 400 by GlobalExceptionHandler.
 */
public class RestaurantUpdateException extends RestaurantBookingException {
    public RestaurantUpdateException() {
    }

    public RestaurantUpdateException(String message) {
        super(message);
    }

    public RestaurantUpdateException(String message, Throwable cause) {
        super(message, cause);
    }

    public RestaurantUpdateException(Throwable cause) {
        super(cause);
    }

    public RestaurantUpdateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
