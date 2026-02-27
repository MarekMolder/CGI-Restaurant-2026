package com.example.CGI_Restaurant.domain.dtos.updateResponses;

import com.example.CGI_Restaurant.domain.entities.PreferencePriorityEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBookingPreferenceResponseDto {
    private UUID id;
    private PreferencePriorityEnum priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
