package com.example.CGI_Restaurant.domain.createRequests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateRestaurantRequest {
    private String name;
    private String timezone;
    private String email;
    private String phone;
    private String address;
    private List<CreateSeatingPlanRequest> seatingPlans = new ArrayList<>();
}
