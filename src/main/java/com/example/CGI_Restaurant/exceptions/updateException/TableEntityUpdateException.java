package com.example.CGI_Restaurant.exceptions.updateException;

import com.example.CGI_Restaurant.exceptions.RestaurantBookingException;

public class TableEntityUpdateException extends RestaurantBookingException {
    public TableEntityUpdateException() {
    }

    public TableEntityUpdateException(String message) {
        super(message);
    }

    public TableEntityUpdateException(String message, Throwable cause) {
        super(message, cause);
    }

    public TableEntityUpdateException(Throwable cause) {
        super(cause);
    }

    public TableEntityUpdateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
