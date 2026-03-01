package com.example.CGI_Restaurant.domain.dtos.getResponses;

import com.example.CGI_Restaurant.domain.entities.PreferencePriorityEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
/** API response for a single booking preference (detail view). */
@AllArgsConstructor
@NoArgsConstructor
public class GetBookingPreferenceDetailsResponseDto {
    private UUID id;
    private PreferencePriorityEnum priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
