package com.example.CGI_Restaurant.domain.dtos.updateRequests;

import com.example.CGI_Restaurant.domain.entities.PreferencePriorityEnum;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBookingPreferenceRequestDto {

    @NotNull(message = "Booking preference ID must be provided")
    private UUID id;

    @NotNull(message = "Priority is required")
    private PreferencePriorityEnum priority;
}
