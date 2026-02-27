package com.example.CGI_Restaurant.domain.updateRequests;

import com.example.CGI_Restaurant.domain.entities.PreferencePriorityEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBookingPreferenceRequest {
    private UUID id;
    private PreferencePriorityEnum priority;

}
