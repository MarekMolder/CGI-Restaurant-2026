package com.example.CGI_Restaurant.controllers;

import com.example.CGI_Restaurant.domain.dtos.requests.CreateBookingRequestDto;
import com.example.CGI_Restaurant.domain.dtos.responses.CreateBookingResponseDto;
import com.example.CGI_Restaurant.domain.entities.Booking;
import com.example.CGI_Restaurant.domain.requests.CreateBookingRequest;
import com.example.CGI_Restaurant.mappers.BookingMapper;
import com.example.CGI_Restaurant.security.CustomerDetails;
import com.example.CGI_Restaurant.services.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingMapper bookingMapper;
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<CreateBookingResponseDto> createBooking(
            @AuthenticationPrincipal CustomerDetails currentUser,
            @Valid @RequestBody CreateBookingRequestDto createBookingRequestDto) {
        CreateBookingRequest createBookingRequest = bookingMapper.fromDto(createBookingRequestDto);
        createBookingRequest.setUser(currentUser.getUser());

        Booking createdBooking = bookingService.createBooking(createBookingRequest);
        CreateBookingResponseDto createBookingResponseDto = bookingMapper.toDto(createdBooking);
        return new ResponseEntity<>(createBookingResponseDto, HttpStatus.CREATED);
    }
}
