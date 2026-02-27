package com.example.CGI_Restaurant.domain.dtos.createResponses;

import com.example.CGI_Restaurant.domain.entities.FeatureCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateFeatureResponseDto {
    private UUID id;
    private FeatureCodeEnum code;
    private String name;
    private List<CreateBookingPreferenceResponseDto> bookingPreferences = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
