package com.example.CGI_Restaurant.mappers;

import com.example.CGI_Restaurant.domain.dtos.getResponses.GetZoneDetailsResponseDto;
import com.example.CGI_Restaurant.domain.dtos.listResponses.ListZoneResponseDto;
import com.example.CGI_Restaurant.domain.dtos.createRequests.CreateZoneRequestDto;
import com.example.CGI_Restaurant.domain.dtos.createResponses.CreateZoneResponseDto;
import com.example.CGI_Restaurant.domain.dtos.updateRequests.UpdateZoneRequestDto;
import com.example.CGI_Restaurant.domain.dtos.updateResponses.UpdateZoneResponseDto;
import com.example.CGI_Restaurant.domain.entities.Zone;
import com.example.CGI_Restaurant.domain.createRequests.CreateZoneRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateZoneRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = TableEntityMapper.class)
public interface ZoneMapper {
    CreateZoneRequest fromDto(CreateZoneRequestDto dto);

    CreateZoneResponseDto toDto(Zone zone);

    ListZoneResponseDto toListZoneResponseDto(Zone zone);

    GetZoneDetailsResponseDto toGetZoneDetailsResponseDto(Zone zone);

    UpdateZoneRequest fromDto(UpdateZoneRequestDto dto);

    UpdateZoneResponseDto toUpdateZoneResponseDto(Zone zone);
}
