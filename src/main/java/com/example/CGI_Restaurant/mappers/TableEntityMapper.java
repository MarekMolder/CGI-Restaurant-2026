package com.example.CGI_Restaurant.mappers;

import com.example.CGI_Restaurant.domain.dtos.getResponses.GetTableEntityDetailsResponseDto;
import com.example.CGI_Restaurant.domain.dtos.listResponses.ListTableEntityResponseDto;
import com.example.CGI_Restaurant.domain.dtos.listResponses.TableAvailabilityItemDto;
import com.example.CGI_Restaurant.domain.dtos.createRequests.CreateTableEntityRequestDto;
import com.example.CGI_Restaurant.domain.dtos.createResponses.CreateTableEntityResponseDto;
import com.example.CGI_Restaurant.domain.dtos.updateRequests.UpdateTableEntityRequestDto;
import com.example.CGI_Restaurant.domain.dtos.updateResponses.UpdateTableEntityResponseDto;
import com.example.CGI_Restaurant.domain.entities.TableEntity;
import com.example.CGI_Restaurant.domain.createRequests.CreateTableEntityRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateTableEntityRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper between TableEntity and create/update/list/get DTOs. Also maps to TableAvailabilityItemDto for availability endpoints.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = BookingTableMapper.class)
public interface TableEntityMapper {

    CreateTableEntityRequest fromDto(CreateTableEntityRequestDto dto);
    CreateTableEntityResponseDto toDto(TableEntity tableEntity);
    ListTableEntityResponseDto toListTableEntityResponseDto(TableEntity tableEntity);
    GetTableEntityDetailsResponseDto toGetTableEntityDetailsResponseDto(TableEntity tableEntity);
    UpdateTableEntityRequest fromDto(UpdateTableEntityRequestDto dto);
    UpdateTableEntityResponseDto toUpdateTableEntityResponseDto(TableEntity tableEntity);

    /** Builds an availability DTO for a single table with zone info, availability flag and recommendation score. */
    @Mapping(target = "zoneId", source = "entity.zone.id")
    @Mapping(target = "zoneName", source = "entity.zone.name")
    @Mapping(target = "zoneType", source = "entity.zone.type")
    @Mapping(target = "available", source = "available")
    @Mapping(target = "recommendationScore", source = "recommendationScore")
    @Mapping(target = "tableIds", expression = "java(java.util.List.of(entity.getId()))")
    @Mapping(target = "combined", constant = "false")
    TableAvailabilityItemDto toTableAvailabilityItemDto(TableEntity entity, boolean available, Integer recommendationScore);
}
