package com.example.CGI_Restaurant.domain.dtos.requests;

import com.example.CGI_Restaurant.domain.entities.PreferencePriorityEnum;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateBookingPreferenceRequestDto {

    @NotNull(message = "Priority is required")
    private PreferencePriorityEnum priority;
}
