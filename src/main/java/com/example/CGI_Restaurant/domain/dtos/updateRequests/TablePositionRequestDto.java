package com.example.CGI_Restaurant.domain.dtos.updateRequests;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Request body for PATCH position (drag-and-drop on floor plan). */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TablePositionRequestDto {
    @NotNull(message = "X is required")
    private Double x;
    @NotNull(message = "Y is required")
    private Double y;
}
