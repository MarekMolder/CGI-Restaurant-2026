package com.example.CGI_Restaurant.domain.dtos.createResponses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateRestaurantResponseDto {
    private UUID id;
    private String name;
    private String timezone;
    private String email;
    private String phone;
    private String address;
    private List<CreateSeatingPlanResponseDto> seatingPlans = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
