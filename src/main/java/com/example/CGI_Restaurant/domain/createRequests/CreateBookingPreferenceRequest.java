package com.example.CGI_Restaurant.domain.createRequests;

import com.example.CGI_Restaurant.domain.entities.PreferencePriorityEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/** Request to attach a feature preference (with priority) to a booking. */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateBookingPreferenceRequest {
    private UUID bookingId;
    private UUID featureId;
    private PreferencePriorityEnum priority;
}
