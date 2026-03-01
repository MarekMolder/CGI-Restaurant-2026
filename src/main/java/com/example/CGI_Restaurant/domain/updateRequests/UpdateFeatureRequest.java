package com.example.CGI_Restaurant.domain.updateRequests;


import com.example.CGI_Restaurant.domain.createRequests.CreateBookingPreferenceRequest;
import com.example.CGI_Restaurant.domain.entities.FeatureCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Request to update a feature (id, code, name). */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateFeatureRequest {
    private UUID id;
    private FeatureCodeEnum code;
    private String name;
    private List<UpdateBookingPreferenceRequest> bookingPreferences = new ArrayList<>();

}
