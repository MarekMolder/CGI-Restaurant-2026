package com.example.CGI_Restaurant.domain.dtos.requests;

import com.example.CGI_Restaurant.domain.entities.FeatureCodeEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateFeatureRequestDto {

    @NotNull(message = "Feature code is required")
    private FeatureCodeEnum code;

    @NotBlank(message = "Feature name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    @Valid
    private List<CreateBookingPreferenceRequestDto> bookingPreferences;
}
