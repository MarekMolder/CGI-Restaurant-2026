package com.example.CGI_Restaurant.domain.dtos.createRequests;

import com.example.CGI_Restaurant.domain.entities.PreferencePriorityEnum;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
/** API request body for creating a booking preference. */
@AllArgsConstructor
@NoArgsConstructor
public class CreateBookingPreferenceRequestDto {
    private UUID bookingId;
    @NotNull(message = "Feature ID is required")
    private UUID featureId;
    @NotNull(message = "Priority is required")
    private PreferencePriorityEnum priority;
}
