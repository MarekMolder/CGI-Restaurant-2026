package com.example.CGI_Restaurant.domain.requests;

import com.example.CGI_Restaurant.domain.entities.ZoneTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateZoneRequest {

    private String name;
    private ZoneTypeEnum type;
    private String color;
    private List<CreateTableEntityRequest> tableEntities = new ArrayList<>();
}
