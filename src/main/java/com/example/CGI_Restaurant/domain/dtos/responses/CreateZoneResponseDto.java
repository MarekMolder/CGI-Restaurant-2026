package com.example.CGI_Restaurant.domain.dtos.responses;

import com.example.CGI_Restaurant.domain.entities.ZoneTypeEnum;
import com.example.CGI_Restaurant.domain.requests.CreateTableEntityRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateZoneResponseDto {
    private UUID id;
    private String name;
    private ZoneTypeEnum type;
    private String color;
    private List<CreateTableEntityResponseDto> tableEntities = new ArrayList<>();
}
