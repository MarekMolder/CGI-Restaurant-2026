package com.example.CGI_Restaurant.domain.dtos.createRequests;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
/** API request body for linking a table to a booking. */
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingTableRequestDto {
    private UUID bookingId;
    @NotNull(message = "Table entity ID is required")
    private UUID tableEntityId;
}
