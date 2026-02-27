package com.example.CGI_Restaurant.domain.dtos.createRequests;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingTableRequestDto {
    /** Omitted when nested in CreateBookingRequest (server sets it); required when POST /booking-tables */
    private UUID bookingId;
    @NotNull(message = "Table entity ID is required")
    private UUID tableEntityId;
}
