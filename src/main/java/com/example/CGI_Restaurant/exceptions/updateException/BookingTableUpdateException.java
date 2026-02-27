package com.example.CGI_Restaurant.exceptions.updateException;

import com.example.CGI_Restaurant.exceptions.RestaurantBookingException;

public class BookingTableUpdateException extends RestaurantBookingException {
    public BookingTableUpdateException() {
    }

    public BookingTableUpdateException(String message) {
        super(message);
    }

    public BookingTableUpdateException(String message, Throwable cause) {
        super(message, cause);
    }

    public BookingTableUpdateException(Throwable cause) {
        super(cause);
    }

    public BookingTableUpdateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
