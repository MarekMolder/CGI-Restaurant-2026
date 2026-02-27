package com.example.CGI_Restaurant.mappers;

import com.example.CGI_Restaurant.domain.dtos.getResponses.GetSeatingPlanDetailsResponseDto;
import com.example.CGI_Restaurant.domain.dtos.listResponses.ListSeatingPlanResponseDto;
import com.example.CGI_Restaurant.domain.dtos.createRequests.CreateSeatingPlanRequestDto;
import com.example.CGI_Restaurant.domain.dtos.createResponses.CreateSeatingPlanResponseDto;
import com.example.CGI_Restaurant.domain.dtos.updateRequests.UpdateSeatingPlanRequestDto;
import com.example.CGI_Restaurant.domain.dtos.updateResponses.UpdateSeatingPlanResponseDto;
import com.example.CGI_Restaurant.domain.entities.SeatingPlan;
import com.example.CGI_Restaurant.domain.createRequests.CreateSeatingPlanRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateSeatingPlanRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SeatingPlanMapper {
    CreateSeatingPlanRequest fromDto(CreateSeatingPlanRequestDto dto);

    CreateSeatingPlanResponseDto toDto(SeatingPlan seatingPlan);

    ListSeatingPlanResponseDto toListSeatingPlanResponseDto(SeatingPlan seatingPlan);

    GetSeatingPlanDetailsResponseDto toGetSeatingPlanDetailsResponseDto(SeatingPlan seatingPlan);

    UpdateSeatingPlanRequest fromDto(UpdateSeatingPlanRequestDto dto);

    UpdateSeatingPlanResponseDto toUpdateSeatingPlanResponseDto(SeatingPlan seatingPlan);
}
