package com.example.CGI_Restaurant.domain.updateRequests;

import com.example.CGI_Restaurant.domain.entities.PreferencePriorityEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/** Request to update a booking preference (id and new priority). */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBookingPreferenceRequest {
    private UUID id;
    private PreferencePriorityEnum priority;

}
