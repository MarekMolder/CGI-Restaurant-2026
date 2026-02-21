package com.example.CGI_Restaurant.mappers;

import com.example.CGI_Restaurant.domain.dtos.requests.CreateZoneRequestDto;
import com.example.CGI_Restaurant.domain.dtos.responses.CreateZoneResponseDto;
import com.example.CGI_Restaurant.domain.entities.Zone;
import com.example.CGI_Restaurant.domain.requests.CreateZoneRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ZoneMapper {
    CreateZoneRequest fromDto(CreateZoneRequestDto dto);

    CreateZoneResponseDto toDto(Zone zone);
}
