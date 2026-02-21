package com.example.CGI_Restaurant.domain.dtos.responses;

import com.example.CGI_Restaurant.domain.entities.SeatingPlanTypeEnum;
import com.example.CGI_Restaurant.domain.requests.CreateTableEntityRequest;
import com.example.CGI_Restaurant.domain.requests.CreateZoneRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
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
}
