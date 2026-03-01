package com.example.CGI_Restaurant.exceptions.notFoundExceptions;

/**
 * Thrown when a menu item is not found by ID. Mapped to 404 by GlobalExceptionHandler.
 */
public class MenuItemNotFoundException extends RuntimeException {

    public MenuItemNotFoundException(String message) {
        super(message);
    }
}
