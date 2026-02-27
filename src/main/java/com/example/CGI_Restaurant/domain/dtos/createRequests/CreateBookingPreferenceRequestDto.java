package com.example.CGI_Restaurant.domain.dtos.createRequests;

import com.example.CGI_Restaurant.domain.entities.PreferencePriorityEnum;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateBookingPreferenceRequestDto {
    /** Omitted when nested in CreateBookingRequest (server sets it); required when POST /booking-preferences */
    private UUID bookingId;
    @NotNull(message = "Feature ID is required")
    private UUID featureId;
    @NotNull(message = "Priority is required")
    private PreferencePriorityEnum priority;
}
