package com.example.CGI_Restaurant.services;

import java.time.LocalDateTime;

public interface RestaurantHoursService {

    boolean isWithinOpeningHours(LocalDateTime start, LocalDateTime end);

    int getBookingDurationHours();
}
