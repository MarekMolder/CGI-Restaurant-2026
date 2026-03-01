package com.example.CGI_Restaurant.domain.dtos.getResponses;

import com.example.CGI_Restaurant.domain.dtos.listResponses.ListTableEntityResponseDto;
import com.example.CGI_Restaurant.domain.dtos.listResponses.ListZoneResponseDto;
import com.example.CGI_Restaurant.domain.entities.SeatingPlanTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
/** API response for a single seating plan (detail view). */
@AllArgsConstructor
@NoArgsConstructor
public class GetSeatingPlanDetailsResponseDto {
    private UUID id;
    private String name;
    private SeatingPlanTypeEnum type;
    private double width;
    private double height;
    private String backgroundSVG;
    private boolean active;
    private int version;
    private List<GetTableEntityDetailsResponseDto> tableEntities = new ArrayList<>();
    private List<GetZoneDetailsResponseDto> zones = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
