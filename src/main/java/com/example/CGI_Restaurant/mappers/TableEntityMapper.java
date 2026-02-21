package com.example.CGI_Restaurant.mappers;

import com.example.CGI_Restaurant.domain.dtos.requests.CreateTableEntityRequestDto;
import com.example.CGI_Restaurant.domain.dtos.responses.CreateTableEntityResponseDto;
import com.example.CGI_Restaurant.domain.entities.TableEntity;
import com.example.CGI_Restaurant.domain.requests.CreateTableEntityRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TableEntityMapper {

    CreateTableEntityRequest fromDto(CreateTableEntityRequestDto dto);

    CreateTableEntityResponseDto toDto(TableEntity tableEntity);
}
