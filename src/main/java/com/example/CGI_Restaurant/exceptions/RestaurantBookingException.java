package com.example.CGI_Restaurant.exceptions;

/**
 * Thrown when a booking request is invalid: outside opening hours, wrong duration,
 * tables already booked, or tables not adjacent. Mapped to 400 by GlobalExceptionHandler.
 */
public class RestaurantBookingException extends RuntimeException {

    public RestaurantBookingException() {
    }

    public RestaurantBookingException(String message) {
        super(message);
    }

    public RestaurantBookingException(String message, Throwable cause) {
        super(message, cause);
    }

    public RestaurantBookingException(Throwable cause) {
        super(cause);
    }

    public RestaurantBookingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
