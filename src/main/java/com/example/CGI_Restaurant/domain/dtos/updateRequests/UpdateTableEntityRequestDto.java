package com.example.CGI_Restaurant.domain.dtos.updateRequests;

import com.example.CGI_Restaurant.domain.entities.TableShapeEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTableEntityRequestDto {

    @NotNull(message = "Table entity ID must be provided")
    private UUID id;

    @NotBlank(message = "Table label is required")
    @Size(max = 50, message = "Label must not exceed 50 characters")
    private String label;

    @NotNull(message = "Capacity is required")
    @jakarta.validation.constraints.Min(value = 1, message = "Capacity must be at least 1")
    @jakarta.validation.constraints.Max(value = 100, message = "Capacity must not exceed 100")
    private Integer capacity;

    @NotNull(message = "Minimum party size is required")
    @jakarta.validation.constraints.Min(value = 0, message = "Minimum party size cannot be negative")
    private Integer minPartySize;

    @NotNull(message = "Table shape is required")
    private TableShapeEnum shape;

    @NotNull(message = "X position is required")
    private Double x;

    @NotNull(message = "Y position is required")
    private Double y;

    @NotNull(message = "Width is required")
    @jakarta.validation.constraints.Positive(message = "Width must be greater than 0")
    private Double width;

    @NotNull(message = "Height is required")
    @jakarta.validation.constraints.Positive(message = "Height must be greater than 0")
    private Double height;

    @NotNull(message = "Rotation degree is required")
    @jakarta.validation.constraints.Min(value = 0, message = "Rotation must be between 0 and 360")
    @jakarta.validation.constraints.Max(value = 360, message = "Rotation must be between 0 and 360")
    private Integer rotationDegree;

    private boolean active;

    private List<UUID> adjacentTableIds = new ArrayList<>();
}
