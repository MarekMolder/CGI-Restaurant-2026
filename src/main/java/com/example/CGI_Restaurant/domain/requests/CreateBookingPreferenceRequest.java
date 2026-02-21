package com.example.CGI_Restaurant.domain.requests;

import com.example.CGI_Restaurant.domain.entities.PreferencePriorityEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateBookingPreferenceRequest {

    private PreferencePriorityEnum priority;

}
