package com.example.CGI_Restaurant.controllers;

import com.example.CGI_Restaurant.domain.dtos.createRequests.CreateBookingPreferenceRequestDto;
import com.example.CGI_Restaurant.domain.dtos.createResponses.CreateBookingPreferenceResponseDto;
import com.example.CGI_Restaurant.domain.dtos.getResponses.GetBookingPreferenceDetailsResponseDto;
import com.example.CGI_Restaurant.domain.dtos.listResponses.ListBookingPreferenceResponseDto;
import com.example.CGI_Restaurant.domain.dtos.updateRequests.UpdateBookingPreferenceRequestDto;
import com.example.CGI_Restaurant.domain.dtos.updateResponses.UpdateBookingPreferenceResponseDto;
import com.example.CGI_Restaurant.domain.entities.BookingPreference;
import com.example.CGI_Restaurant.domain.createRequests.CreateBookingPreferenceRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateBookingPreferenceRequest;
import com.example.CGI_Restaurant.mappers.BookingPreferenceMapper;
import com.example.CGI_Restaurant.services.BookingPreferenceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST API for booking preferences (e.g. feature preferences linked to a booking). Full CRUD.
 */
@RestController
@RequestMapping(path = "/api/v1/booking-preferences")
@RequiredArgsConstructor
public class BookingPreferenceController {

    private final BookingPreferenceMapper bookingPreferenceMapper;
    private final BookingPreferenceService bookingPreferenceService;

    /** Creates a booking preference (booking id + feature id + priority). */
    @PostMapping
    public ResponseEntity<CreateBookingPreferenceResponseDto> create(@Valid @RequestBody CreateBookingPreferenceRequestDto dto) {
        CreateBookingPreferenceRequest request = bookingPreferenceMapper.fromDto(dto);
        BookingPreference created = bookingPreferenceService.create(request);
        return new ResponseEntity<>(bookingPreferenceMapper.toDto(created), HttpStatus.CREATED);
    }

    /** Returns a paginated list of booking preferences. */
    @GetMapping
    public ResponseEntity<Page<ListBookingPreferenceResponseDto>> list(Pageable pageable) {
        return ResponseEntity.ok(bookingPreferenceService.list(pageable).map(bookingPreferenceMapper::toListBookingPreferenceResponseDto));
    }

    /** Returns a booking preference by ID, or 404. */
    @GetMapping("/{id}")
    public ResponseEntity<GetBookingPreferenceDetailsResponseDto> getById(@PathVariable UUID id) {
        return bookingPreferenceService.getById(id)
                .map(bookingPreferenceMapper::toGetBookingPreferenceDetailsResponseDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** Updates a booking preference. */
    @PutMapping("/{id}")
    public ResponseEntity<UpdateBookingPreferenceResponseDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateBookingPreferenceRequestDto dto) {
        UpdateBookingPreferenceRequest request = bookingPreferenceMapper.fromDto(dto);
        request.setId(id);
        BookingPreference updated = bookingPreferenceService.update(id, request);
        return ResponseEntity.ok(bookingPreferenceMapper.toUpdateBookingPreferenceResponseDto(updated));
    }

    /** Deletes a booking preference by ID. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        bookingPreferenceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
