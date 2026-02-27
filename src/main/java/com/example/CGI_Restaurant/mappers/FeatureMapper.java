package com.example.CGI_Restaurant.mappers;

import com.example.CGI_Restaurant.domain.dtos.getResponses.GetFeatureDetailsResponseDto;
import com.example.CGI_Restaurant.domain.dtos.listResponses.ListFeatureResponseDto;
import com.example.CGI_Restaurant.domain.dtos.createRequests.CreateFeatureRequestDto;
import com.example.CGI_Restaurant.domain.dtos.createResponses.CreateFeatureResponseDto;
import com.example.CGI_Restaurant.domain.dtos.updateRequests.UpdateFeatureRequestDto;
import com.example.CGI_Restaurant.domain.dtos.updateResponses.UpdateFeatureResponseDto;
import com.example.CGI_Restaurant.domain.entities.Feature;
import com.example.CGI_Restaurant.domain.createRequests.CreateFeatureRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateFeatureRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FeatureMapper {
    CreateFeatureRequest fromDto(CreateFeatureRequestDto dto);

    CreateFeatureResponseDto toDto(Feature feature);

    ListFeatureResponseDto toListFeatureResponseDto(Feature feature);

    GetFeatureDetailsResponseDto toGetFeatureDetailsResponseDto(Feature feature);

    UpdateFeatureRequest fromDto(UpdateFeatureRequestDto dto);

    UpdateFeatureResponseDto toUpdateFeatureResponseDto(Feature feature);
}
