package com.example.CGI_Restaurant.mappers;

import com.example.CGI_Restaurant.domain.dtos.getResponses.GetTableEntityDetailsResponseDto;
import com.example.CGI_Restaurant.domain.dtos.listResponses.ListTableEntityResponseDto;
import com.example.CGI_Restaurant.domain.dtos.createRequests.CreateTableEntityRequestDto;
import com.example.CGI_Restaurant.domain.dtos.createResponses.CreateTableEntityResponseDto;
import com.example.CGI_Restaurant.domain.dtos.updateRequests.UpdateTableEntityRequestDto;
import com.example.CGI_Restaurant.domain.dtos.updateResponses.UpdateTableEntityResponseDto;
import com.example.CGI_Restaurant.domain.entities.TableEntity;
import com.example.CGI_Restaurant.domain.createRequests.CreateTableEntityRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateTableEntityRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = BookingTableMapper.class)
public interface TableEntityMapper {

    CreateTableEntityRequest fromDto(CreateTableEntityRequestDto dto);

    CreateTableEntityResponseDto toDto(TableEntity tableEntity);

    ListTableEntityResponseDto toListTableEntityResponseDto(TableEntity tableEntity);

    GetTableEntityDetailsResponseDto toGetTableEntityDetailsResponseDto(TableEntity tableEntity);

    UpdateTableEntityRequest fromDto(UpdateTableEntityRequestDto dto);

    UpdateTableEntityResponseDto toUpdateTableEntityResponseDto(TableEntity tableEntity);
}
