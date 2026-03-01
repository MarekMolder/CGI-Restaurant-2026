package com.example.CGI_Restaurant.domain.dtos.updateResponses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
/** API response after updating a booking-table link. */
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBookingTableResponseDto {
    private UUID id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
