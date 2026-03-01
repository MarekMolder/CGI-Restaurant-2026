package com.example.CGI_Restaurant.domain.createRequests;

import com.example.CGI_Restaurant.domain.entities.SeatingPlanTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/** Request to create a seating plan with dimensions, type and optional zones/tables. */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateSeatingPlanRequest {
    private String name;
    private SeatingPlanTypeEnum type;
    private double width;
    private double height;
    private String backgroundSVG;
    private boolean active;
    private int version;
    private List<CreateTableEntityRequest> tableEntities = new ArrayList<>();
    private List<CreateZoneRequest> zones = new ArrayList<>();
}
