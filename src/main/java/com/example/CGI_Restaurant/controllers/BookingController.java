package com.example.CGI_Restaurant.controllers;

import com.example.CGI_Restaurant.domain.dtos.getResponses.GetBookingDetailsResponseDto;
import com.example.CGI_Restaurant.domain.dtos.listResponses.ListBookingResponseDto;
import com.example.CGI_Restaurant.domain.dtos.createRequests.CreateBookingRequestDto;
import com.example.CGI_Restaurant.domain.dtos.createResponses.CreateBookingResponseDto;
import com.example.CGI_Restaurant.domain.dtos.updateRequests.UpdateBookingRequestDto;
import com.example.CGI_Restaurant.domain.dtos.updateResponses.UpdateBookingResponseDto;
import com.example.CGI_Restaurant.domain.entities.Booking;
import com.example.CGI_Restaurant.domain.entities.UserRoleEnum;
import com.example.CGI_Restaurant.domain.createRequests.CreateBookingRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateBookingRequest;
import com.example.CGI_Restaurant.mappers.BookingMapper;
import com.example.CGI_Restaurant.security.CustomerDetails;
import com.example.CGI_Restaurant.services.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

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

    @GetMapping
    public ResponseEntity<Page<ListBookingResponseDto>> listBookings(
            @AuthenticationPrincipal CustomerDetails currentUser,
            Pageable pageable
    ) {
        Page<Booking> bookings;

        if (currentUser.getRole() == UserRoleEnum.ADMIN) {
            bookings = bookingService.listBookingsForAdmin(pageable);
        } else {
            bookings = bookingService.listBookingForCustomer(currentUser.getId(), pageable);
        }

        return ResponseEntity.ok(bookings.map(bookingMapper::toListBookingResponseDto));
    }

    @GetMapping(path = "/{bookingId}")
    public ResponseEntity<GetBookingDetailsResponseDto> getBooking(
            @AuthenticationPrincipal CustomerDetails currentUser,
            @PathVariable UUID bookingId
    ) {
        Optional<Booking> booking;

        if (currentUser.getRole() == UserRoleEnum.ADMIN) {
            booking = bookingService.getBooking(bookingId);
        } else {
            booking = bookingService.getBookingForCustomer(bookingId, currentUser.getId());
        }

        return booking.map(bookingMapper::toGetBookingDetailsResponseDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }

    @PutMapping(path = "/{bookingId}")
    public ResponseEntity<UpdateBookingResponseDto> updateBooking(
            @AuthenticationPrincipal CustomerDetails currentUser,
            @PathVariable UUID bookingId,
            @Valid @RequestBody UpdateBookingRequestDto updateBookingRequestDto) {
        UpdateBookingRequest updateBookingRequest = bookingMapper.fromDto(updateBookingRequestDto);

        Booking updatedBooking;

        if (currentUser.getRole() == UserRoleEnum.ADMIN) {
            updatedBooking = bookingService.updateBooking(
                    bookingId, updateBookingRequest
            );
        } else {
            updatedBooking = bookingService.updateBookingForCustomer(
                    bookingId, currentUser.getId(), updateBookingRequest
            );
        }

        UpdateBookingResponseDto updateBookingResponseDto = bookingMapper.toUpdateBookingResponseDto(updatedBooking);

        return ResponseEntity.ok(updateBookingResponseDto);
    }

    @DeleteMapping(path = "/{bookingId}")
    public ResponseEntity<Void> deleteBooking(
            @AuthenticationPrincipal CustomerDetails currentUser,
            @PathVariable UUID bookingId
    ) {
        if (currentUser.getRole() == UserRoleEnum.ADMIN) {
            bookingService.deleteBooking(bookingId);
        } else {
            bookingService.deleteBookingForCustomer(bookingId, currentUser.getId());
        }
        return ResponseEntity.noContent().build();
    }

}
