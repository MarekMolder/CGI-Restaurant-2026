package com.example.CGI_Restaurant.domain.dtos.createRequests;

import com.example.CGI_Restaurant.domain.entities.SeatingPlanTypeEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateSeatingPlanRequestDto {

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

    @Valid
    private List<CreateTableEntityRequestDto> tableEntities = new ArrayList<>();

    @Valid
    private List<CreateZoneRequestDto> zones = new ArrayList<>();
}
