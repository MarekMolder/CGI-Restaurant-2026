package com.example.CGI_Restaurant.domain.dtos.createResponses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateBookingTableResponseDto {
    private UUID id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
