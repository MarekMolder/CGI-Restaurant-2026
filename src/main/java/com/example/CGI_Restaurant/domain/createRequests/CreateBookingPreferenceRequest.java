package com.example.CGI_Restaurant.domain.createRequests;

import com.example.CGI_Restaurant.domain.entities.PreferencePriorityEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateBookingPreferenceRequest {
    private UUID bookingId;
    private UUID featureId;
    private PreferencePriorityEnum priority;
}
