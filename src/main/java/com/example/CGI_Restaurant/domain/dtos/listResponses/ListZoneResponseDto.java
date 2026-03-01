package com.example.CGI_Restaurant.domain.dtos.listResponses;

import com.example.CGI_Restaurant.domain.entities.ZoneTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
/** API response item for a zone in list endpoints. */
@AllArgsConstructor
@NoArgsConstructor
public class ListZoneResponseDto {
    private UUID id;
    private String name;
    private ZoneTypeEnum type;
    private String color;
    private List<ListTableEntityResponseDto> tableEntities = new ArrayList<>();
}
