package com.example.CGI_Restaurant.domain.dtos.listResponses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListBookingTableResponseDto {

    private UUID id;
}
