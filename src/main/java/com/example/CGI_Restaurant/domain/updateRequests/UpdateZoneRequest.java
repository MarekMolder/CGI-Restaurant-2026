package com.example.CGI_Restaurant.domain.updateRequests;

import com.example.CGI_Restaurant.domain.createRequests.CreateTableEntityRequest;
import com.example.CGI_Restaurant.domain.entities.ZoneTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateZoneRequest {
    private UUID id;
    private String name;
    private ZoneTypeEnum type;
    private String color;
    private List<UpdateTableEntityRequest> tableEntities = new ArrayList<>();
}
