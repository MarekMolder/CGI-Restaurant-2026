package com.example.CGI_Restaurant.exceptions.updateException;

import com.example.CGI_Restaurant.exceptions.RestaurantBookingException;

public class BookingUpdateException extends RestaurantBookingException {
    public BookingUpdateException() {
    }

    public BookingUpdateException(String message) {
        super(message);
    }

    public BookingUpdateException(String message, Throwable cause) {
        super(message, cause);
    }

    public BookingUpdateException(Throwable cause) {
        super(cause);
    }

    public BookingUpdateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
