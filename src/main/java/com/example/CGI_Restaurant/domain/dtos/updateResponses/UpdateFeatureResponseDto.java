package com.example.CGI_Restaurant.domain.dtos.updateResponses;

import com.example.CGI_Restaurant.domain.dtos.createResponses.CreateBookingPreferenceResponseDto;
import com.example.CGI_Restaurant.domain.entities.FeatureCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
/** API response after updating a feature. */
@AllArgsConstructor
@NoArgsConstructor
public class UpdateFeatureResponseDto {
    private UUID id;
    private FeatureCodeEnum code;
    private String name;
    private List<UpdateBookingPreferenceResponseDto> bookingPreferences = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
