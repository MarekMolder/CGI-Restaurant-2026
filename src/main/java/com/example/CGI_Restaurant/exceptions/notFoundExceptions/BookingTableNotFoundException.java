package com.example.CGI_Restaurant.exceptions.notFoundExceptions;

import com.example.CGI_Restaurant.exceptions.RestaurantBookingException;

public class BookingTableNotFoundException extends RestaurantBookingException {
    public BookingTableNotFoundException() {
    }

    public BookingTableNotFoundException(String message) {
        super(message);
    }

    public BookingTableNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public BookingTableNotFoundException(Throwable cause) {
        super(cause);
    }

    public BookingTableNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
