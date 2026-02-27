package com.example.CGI_Restaurant.controllers;

import com.example.CGI_Restaurant.domain.dtos.createRequests.CreateBookingTableRequestDto;
import com.example.CGI_Restaurant.domain.dtos.createResponses.CreateBookingTableResponseDto;
import com.example.CGI_Restaurant.domain.dtos.getResponses.GetBookingTableDetailsResponseDto;
import com.example.CGI_Restaurant.domain.dtos.listResponses.ListBookingTableResponseDto;
import com.example.CGI_Restaurant.domain.dtos.updateRequests.UpdateBookingTableRequestDto;
import com.example.CGI_Restaurant.domain.dtos.updateResponses.UpdateBookingTableResponseDto;
import com.example.CGI_Restaurant.domain.entities.BookingTable;
import com.example.CGI_Restaurant.domain.createRequests.CreateBookingTableRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateBookingTableRequest;
import com.example.CGI_Restaurant.mappers.BookingTableMapper;
import com.example.CGI_Restaurant.services.BookingTableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/booking-tables")
@RequiredArgsConstructor
public class BookingTableController {

    private final BookingTableMapper bookingTableMapper;
    private final BookingTableService bookingTableService;

    @PostMapping
    public ResponseEntity<CreateBookingTableResponseDto> create(@Valid @RequestBody CreateBookingTableRequestDto dto) {
        CreateBookingTableRequest request = bookingTableMapper.fromDto(dto);
        BookingTable created = bookingTableService.create(request);
        return new ResponseEntity<>(bookingTableMapper.toDto(created), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<ListBookingTableResponseDto>> list(Pageable pageable) {
        return ResponseEntity.ok(bookingTableService.list(pageable).map(bookingTableMapper::toListBookingTableResponseDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetBookingTableDetailsResponseDto> getById(@PathVariable UUID id) {
        return bookingTableService.getById(id)
                .map(bookingTableMapper::toGetBookingTableDetailsResponseDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdateBookingTableResponseDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateBookingTableRequestDto dto) {
        UpdateBookingTableRequest request = bookingTableMapper.fromDto(dto);
        request.setId(id);
        BookingTable updated = bookingTableService.update(id, request);
        return ResponseEntity.ok(bookingTableMapper.toUpdateBookingTableResponseDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        bookingTableService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
