package com.example.CGI_Restaurant.domain.dtos.updateResponses;

import com.example.CGI_Restaurant.domain.dtos.createResponses.CreateSeatingPlanResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
/** API response after updating a restaurant. */
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRestaurantResponseDto {
    private UUID id;
    private String name;
    private String timezone;
    private String email;
    private String phone;
    private String address;
    private List<UpdateSeatingPlanResponseDto> seatingPlans = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
