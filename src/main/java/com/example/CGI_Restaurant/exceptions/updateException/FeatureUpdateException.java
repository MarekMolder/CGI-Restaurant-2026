package com.example.CGI_Restaurant.exceptions.updateException;

import com.example.CGI_Restaurant.exceptions.RestaurantBookingException;

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
