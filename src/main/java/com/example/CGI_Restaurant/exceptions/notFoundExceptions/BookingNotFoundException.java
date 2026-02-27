package com.example.CGI_Restaurant.exceptions.notFoundExceptions;

import com.example.CGI_Restaurant.exceptions.RestaurantBookingException;

public class BookingNotFoundException extends RestaurantBookingException {
    public BookingNotFoundException() {
    }

    public BookingNotFoundException(String message) {
        super(message);
    }

    public BookingNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public BookingNotFoundException(Throwable cause) {
        super(cause);
    }

    public BookingNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
