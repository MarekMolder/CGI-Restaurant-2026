package com.example.CGI_Restaurant.domain.dtos.listResponses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListRestaurantResponseDto {
    private UUID id;
    private String name;
    private String timezone;
    private String email;
    private String phone;
    private String address;
    private List<ListSeatingPlanResponseDto> seatingPlans = new ArrayList<>();
}
