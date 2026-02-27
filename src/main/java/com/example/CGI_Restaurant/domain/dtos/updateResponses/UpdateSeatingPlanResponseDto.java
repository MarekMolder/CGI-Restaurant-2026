package com.example.CGI_Restaurant.domain.dtos.updateResponses;

import com.example.CGI_Restaurant.domain.dtos.createResponses.CreateTableEntityResponseDto;
import com.example.CGI_Restaurant.domain.dtos.createResponses.CreateZoneResponseDto;
import com.example.CGI_Restaurant.domain.entities.SeatingPlanTypeEnum;
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
public class UpdateSeatingPlanResponseDto {
    private UUID id;
    private String name;
    private SeatingPlanTypeEnum type;
    private double width;
    private double height;
    private String backgroundSVG;
    private boolean active;
    private int version;
    private List<UpdateTableEntityResponseDto> tableEntities = new ArrayList<>();
    private List<UpdateZoneResponseDto> zones = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
