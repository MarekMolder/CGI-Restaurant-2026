package com.example.CGI_Restaurant.domain.dtos.updateRequests;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class UpdateBookingTableRequestDto {

    @NotNull(message = "Booking table ID must be provided")
    private UUID id;
}
