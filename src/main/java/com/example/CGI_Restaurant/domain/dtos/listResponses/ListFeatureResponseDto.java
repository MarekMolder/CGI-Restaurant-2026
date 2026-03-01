package com.example.CGI_Restaurant.domain.dtos.listResponses;

import com.example.CGI_Restaurant.domain.entities.FeatureCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
/** API response item for a feature in list endpoints. */
@AllArgsConstructor
@NoArgsConstructor
public class ListFeatureResponseDto {

    private UUID id;
    private FeatureCodeEnum code;
    private String name;
    private List<ListBookingPreferenceResponseDto> bookingPreferences = new ArrayList<>();
}
