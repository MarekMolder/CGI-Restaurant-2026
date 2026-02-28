package com.example.CGI_Restaurant.exceptions;

public class NoMoreTablesException extends RuntimeException {

    public NoMoreTablesException() {
    }

    public NoMoreTablesException(String message) {
        super(message);
    }

    public NoMoreTablesException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoMoreTablesException(Throwable cause) {
        super(cause);
    }

    public NoMoreTablesException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
