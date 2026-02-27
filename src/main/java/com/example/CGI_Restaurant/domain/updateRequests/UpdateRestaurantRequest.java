package com.example.CGI_Restaurant.domain.updateRequests;

import com.example.CGI_Restaurant.domain.updateRequests.UpdateSeatingPlanRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRestaurantRequest {
    private UUID id;
    private String name;
    private String timezone;
    private String email;
    private String phone;
    private String address;
    private List<UpdateSeatingPlanRequest> seatingPlans = new ArrayList<>();
}
