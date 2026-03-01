package com.example.CGI_Restaurant.services.impl;

import com.example.CGI_Restaurant.services.RestaurantHoursService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Reads opening hours and booking duration from configuration; validates that a time range falls within opening hours (weekday vs weekend).
 */
@Service
public class RestaurantHoursServiceImpl implements RestaurantHoursService {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    @Value("${restaurant.hours.weekday.open:10:00}")
    private String weekdayOpenStr;

    @Value("${restaurant.hours.weekday.close:18:00}")
    private String weekdayCloseStr;

    @Value("${restaurant.hours.weekend.open:10:00}")
    private String weekendOpenStr;

    @Value("${restaurant.hours.weekend.close:22:00}")
    private String weekendCloseStr;

    @Value("${restaurant.booking.duration-hours:2}")
    private int bookingDurationHours;

    @Override
    public boolean isWithinOpeningHours(LocalDateTime start, LocalDateTime end) {
        if (!start.toLocalDate().equals(end.toLocalDate())) {
            return false;
        }
        LocalTime weekdayOpen = LocalTime.parse(weekdayOpenStr, TIME_FORMAT);
        LocalTime weekdayClose = LocalTime.parse(weekdayCloseStr, TIME_FORMAT);
        LocalTime weekendOpen = LocalTime.parse(weekendOpenStr, TIME_FORMAT);
        LocalTime weekendClose = LocalTime.parse(weekendCloseStr, TIME_FORMAT);

        LocalTime startTime = start.toLocalTime();
        LocalTime endTime = end.toLocalTime();
        DayOfWeek startDay = start.toLocalDate().getDayOfWeek();
        DayOfWeek endDay = end.toLocalDate().getDayOfWeek();

        LocalTime startOpen = isWeekend(startDay) ? weekendOpen : weekdayOpen;
        LocalTime startClose = isWeekend(startDay) ? weekendClose : weekdayClose;
        if (startTime.isBefore(startOpen) || !startTime.isBefore(startClose)) {
            return false;
        }
        LocalTime endClose = isWeekend(endDay) ? weekendClose : weekdayClose;
        if (endTime.isAfter(endClose)) {
            return false;
        }
        return true;
    }

    @Override
    public int getBookingDurationHours() {
        return bookingDurationHours;
    }

    private static boolean isWeekend(DayOfWeek day) {
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }
}
