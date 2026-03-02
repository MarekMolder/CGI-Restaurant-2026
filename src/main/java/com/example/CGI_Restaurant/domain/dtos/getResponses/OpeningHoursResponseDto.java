package com.example.CGI_Restaurant.domain.dtos.getResponses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API response for restaurant opening hours and booking slot duration.
 * Times are in "HH:mm" format.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpeningHoursResponseDto {
    private String weekdayOpen;
    private String weekdayClose;
    private String weekendOpen;
    private String weekendClose;
    private int bookingDurationHours;
}
