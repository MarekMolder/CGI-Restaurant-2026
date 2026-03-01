package com.example.CGI_Restaurant.exceptions;

/**
 * Thrown when a QR code is not found by ID. Mapped to 404 by GlobalExceptionHandler.
 */
public class QrCodeNotFoundException extends RuntimeException {

    public QrCodeNotFoundException() {
    }

    public QrCodeNotFoundException(String message) {
        super(message);
    }

    public QrCodeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public QrCodeNotFoundException(Throwable cause) {
        super(cause);
    }

    public QrCodeNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
