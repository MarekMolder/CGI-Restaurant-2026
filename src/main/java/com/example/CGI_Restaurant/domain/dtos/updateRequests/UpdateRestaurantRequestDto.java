package com.example.CGI_Restaurant.domain.dtos.updateRequests;

import com.example.CGI_Restaurant.domain.dtos.createRequests.CreateSeatingPlanRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRestaurantRequestDto {

    @NotNull(message = "Restaurant ID must be provided")
    private UUID id;

    @NotBlank(message = "Restaurant name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    @NotBlank(message = "Timezone is required")
    @Size(max = 100, message = "Timezone must not exceed 100 characters")
    private String timezone;

    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email address")
    @Size(max = 255)
    private String email;

    @NotBlank(message = "Phone number is required")
    @Size(max = 50, message = "Phone must not exceed 50 characters")
    private String phone;

    @NotBlank(message = "Address is required")
    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;

    @Valid
    private List<UpdateSeatingPlanRequestDto> seatingPlans = new ArrayList<>();
}
