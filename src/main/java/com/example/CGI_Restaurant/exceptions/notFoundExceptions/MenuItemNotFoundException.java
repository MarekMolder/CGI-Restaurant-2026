package com.example.CGI_Restaurant.exceptions.notFoundExceptions;

public class MenuItemNotFoundException extends RuntimeException {

    public MenuItemNotFoundException(String message) {
        super(message);
    }
}
