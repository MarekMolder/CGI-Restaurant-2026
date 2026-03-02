package com.example.CGI_Restaurant.controllers;

import com.example.CGI_Restaurant.domain.dtos.getResponses.OpeningHoursResponseDto;
import com.example.CGI_Restaurant.services.RestaurantHoursService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API for restaurant opening hours and booking duration (public).
 * Used by the frontend to generate available time slots.
 */
@RestController
@RequestMapping(path = "/api/v1/restaurant-hours")
@RequiredArgsConstructor
public class RestaurantHoursController {

    private final RestaurantHoursService restaurantHoursService;

    /** Returns opening hours (weekday/weekend open/close) and booking duration in hours. */
    @GetMapping
    public ResponseEntity<OpeningHoursResponseDto> getOpeningHours() {
        return ResponseEntity.ok(restaurantHoursService.getOpeningHours());
    }
}
