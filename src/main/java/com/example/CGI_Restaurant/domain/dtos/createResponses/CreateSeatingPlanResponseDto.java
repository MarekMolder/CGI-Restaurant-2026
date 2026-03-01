package com.example.CGI_Restaurant.domain.dtos.createResponses;

import com.example.CGI_Restaurant.domain.entities.SeatingPlanTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
/** API response after creating a seating plan. */
@AllArgsConstructor
@NoArgsConstructor
public class CreateSeatingPlanResponseDto {
    private UUID id;
    private String name;
    private SeatingPlanTypeEnum type;
    private double width;
    private double height;
    private String backgroundSVG;
    private boolean active;
    private int version;
    private List<CreateTableEntityResponseDto> tableEntities = new ArrayList<>();
    private List<CreateZoneResponseDto> zones = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
