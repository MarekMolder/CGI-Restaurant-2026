package com.example.CGI_Restaurant.domain.updateRequests;

import com.example.CGI_Restaurant.domain.updateRequests.UpdateTableEntityRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateZoneRequest;
import com.example.CGI_Restaurant.domain.entities.SeatingPlanTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateSeatingPlanRequest {
    private UUID id;
    private String name;
    private SeatingPlanTypeEnum type;
    private double width;
    private double height;
    private String backgroundSVG;
    private boolean active;
    private int version;
    private List<UpdateTableEntityRequest> tableEntities = new ArrayList<>();
    private List<UpdateZoneRequest> zones = new ArrayList<>();
}
