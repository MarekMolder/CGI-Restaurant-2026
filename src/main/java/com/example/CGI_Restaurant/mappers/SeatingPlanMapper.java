package com.example.CGI_Restaurant.mappers;

import com.example.CGI_Restaurant.domain.dtos.requests.CreateSeatingPlanRequestDto;
import com.example.CGI_Restaurant.domain.dtos.responses.CreateSeatingPlanResponseDto;
import com.example.CGI_Restaurant.domain.entities.SeatingPlan;
import com.example.CGI_Restaurant.domain.requests.CreateSeatingPlanRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SeatingPlanMapper {
    CreateSeatingPlanRequest fromDto(CreateSeatingPlanRequestDto dto);

    CreateSeatingPlanResponseDto toDto(SeatingPlan seatingPlan);
}
