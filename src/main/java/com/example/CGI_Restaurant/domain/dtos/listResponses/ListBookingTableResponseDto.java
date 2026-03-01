package com.example.CGI_Restaurant.domain.dtos.listResponses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
/** API response item for a booking-table in list endpoints. */
@AllArgsConstructor
@NoArgsConstructor
public class ListBookingTableResponseDto {

    private UUID id;
}
