package com.example.CGI_Restaurant.domain.dtos.updateRequests;

import com.example.CGI_Restaurant.domain.dtos.createRequests.CreateTableEntityRequestDto;
import com.example.CGI_Restaurant.domain.dtos.createRequests.CreateZoneRequestDto;
import com.example.CGI_Restaurant.domain.entities.SeatingPlanTypeEnum;
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
public class UpdateSeatingPlanRequestDto {

    @NotNull(message = "Seating plan ID must be provided")
    private UUID id;

    @NotBlank(message = "Seating plan name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    @NotNull(message = "Seating plan type is required")
    private SeatingPlanTypeEnum type;

    @NotNull(message = "Width is required")
    @Positive(message = "Width must be greater than 0")
    private Double width;

    @NotNull(message = "Height is required")
    @Positive(message = "Height must be greater than 0")
    private Double height;

    private String backgroundSVG;

    private boolean active;

    @NotNull(message = "Version is required")
    @jakarta.validation.constraints.Min(value = 0, message = "Version must be 0 or greater")
    private Integer version;

    @NotEmpty(message = "At least one table is required")
    @Valid
    private List<UpdateTableEntityRequestDto> tableEntities = new ArrayList<>();

    @NotEmpty(message = "At least one zone is required")
    @Valid
    private List<UpdateZoneRequestDto> zones = new ArrayList<>();
}
