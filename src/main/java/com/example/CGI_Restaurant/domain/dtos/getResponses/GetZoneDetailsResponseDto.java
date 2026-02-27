package com.example.CGI_Restaurant.domain.dtos.getResponses;

import com.example.CGI_Restaurant.domain.dtos.listResponses.ListTableEntityResponseDto;
import com.example.CGI_Restaurant.domain.entities.ZoneTypeEnum;
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
public class GetZoneDetailsResponseDto {
    private UUID id;
    private String name;
    private ZoneTypeEnum type;
    private String color;
    private List<GetTableEntityDetailsResponseDto> tableEntities = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
