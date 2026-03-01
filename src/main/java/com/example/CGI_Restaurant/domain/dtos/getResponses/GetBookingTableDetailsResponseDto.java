package com.example.CGI_Restaurant.domain.dtos.getResponses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
/** API response for a single booking-table (detail view). */
@AllArgsConstructor
@NoArgsConstructor
public class GetBookingTableDetailsResponseDto {
    private UUID id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
