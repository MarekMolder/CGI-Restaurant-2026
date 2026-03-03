package com.example.CGI_Restaurant.domain.dtos.updateRequests;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Request body for PATCH layout (position, size, rotation on floor plan). */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TableLayoutRequestDto {
    @NotNull(message = "X is required")
    private Double x;
    @NotNull(message = "Y is required")
    private Double y;
    @NotNull(message = "Width is required")
    @Positive(message = "Width must be positive")
    private Double width;
    @NotNull(message = "Height is required")
    @Positive(message = "Height must be positive")
    private Double height;
    @NotNull(message = "Rotation degree is required")
    @Min(value = 0, message = "Rotation must be 0-360")
    @Max(value = 360, message = "Rotation must be 0-360")
    private Integer rotationDegree;
}
