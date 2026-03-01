package com.example.CGI_Restaurant.domain.dtos.getResponses;

import com.example.CGI_Restaurant.domain.dtos.getResponses.GetSeatingPlanDetailsResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
/** API response for a single restaurant (detail view). */
@AllArgsConstructor
@NoArgsConstructor
public class GetRestaurantDetailsResponseDto {
    private UUID id;
    private String name;
    private String timezone;
    private String email;
    private String phone;
    private String address;
    private List<GetSeatingPlanDetailsResponseDto> seatingPlans = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
