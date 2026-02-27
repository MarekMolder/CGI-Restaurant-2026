package com.example.CGI_Restaurant.domain.dtos.updateRequests;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBookingTableRequestDto {
    @NotNull(message = "Booking table ID must be provided")
    private UUID id;
}
