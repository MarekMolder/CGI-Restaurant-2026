package com.example.CGI_Restaurant.exceptions.updateException;

import com.example.CGI_Restaurant.exceptions.RestaurantBookingException;

public class BookingPreferenceUpdateException extends RestaurantBookingException {
    public BookingPreferenceUpdateException() {
    }

    public BookingPreferenceUpdateException(String message) {
        super(message);
    }

    public BookingPreferenceUpdateException(String message, Throwable cause) {
        super(message, cause);
    }

    public BookingPreferenceUpdateException(Throwable cause) {
        super(cause);
    }

    public BookingPreferenceUpdateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
