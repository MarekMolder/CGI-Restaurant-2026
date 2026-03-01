package com.example.CGI_Restaurant.services;

import java.time.LocalDateTime;

/** Provides opening hours checks and configured booking duration for the restaurant. */
public interface RestaurantHoursService {

    /** Returns true if the given time range lies entirely within opening hours (same day, within open/close). */
    boolean isWithinOpeningHours(LocalDateTime start, LocalDateTime end);

    /** Returns the configured booking duration in hours (e.g. 2). */
    int getBookingDurationHours();
}
