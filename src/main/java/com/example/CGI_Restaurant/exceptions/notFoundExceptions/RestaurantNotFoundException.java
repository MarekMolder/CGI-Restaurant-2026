package com.example.CGI_Restaurant.exceptions.notFoundExceptions;

import com.example.CGI_Restaurant.exceptions.RestaurantBookingException;

public class RestaurantNotFoundException extends RestaurantBookingException {
    public RestaurantNotFoundException() {
    }

    public RestaurantNotFoundException(String message) {
        super(message);
    }

    public RestaurantNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public RestaurantNotFoundException(Throwable cause) {
        super(cause);
    }

    public RestaurantNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
