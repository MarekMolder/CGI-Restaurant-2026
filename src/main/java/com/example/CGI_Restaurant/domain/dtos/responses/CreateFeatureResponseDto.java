package com.example.CGI_Restaurant.domain.dtos.responses;

import com.example.CGI_Restaurant.domain.entities.FeatureCodeEnum;
import com.example.CGI_Restaurant.domain.requests.CreateBookingPreferenceRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
