package com.example.CGI_Restaurant.domain.createRequests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/** Request to assign a table to a booking. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingTableRequest {
    private UUID bookingId;
    private UUID tableEntityId;
}
