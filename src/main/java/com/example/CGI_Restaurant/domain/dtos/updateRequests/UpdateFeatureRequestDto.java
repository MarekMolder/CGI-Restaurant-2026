package com.example.CGI_Restaurant.domain.dtos.updateRequests;

import com.example.CGI_Restaurant.domain.dtos.createRequests.CreateBookingPreferenceRequestDto;
import com.example.CGI_Restaurant.domain.entities.FeatureCodeEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateFeatureRequestDto {

    @NotNull(message = "Feature ID must be provided")
    private UUID id;

    @NotNull(message = "Feature code is required")
    private FeatureCodeEnum code;

    @NotBlank(message = "Feature name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    @Valid
    private List<UpdateBookingPreferenceRequestDto> bookingPreferences;
}
