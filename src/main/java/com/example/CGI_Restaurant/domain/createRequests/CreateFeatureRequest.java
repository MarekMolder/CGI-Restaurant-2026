package com.example.CGI_Restaurant.domain.createRequests;


import com.example.CGI_Restaurant.domain.entities.FeatureCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateFeatureRequest {

    private FeatureCodeEnum code;
    private String name;
    private List<CreateBookingPreferenceRequest> bookingPreferences = new ArrayList<>();

}
