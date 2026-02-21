package com.example.CGI_Restaurant.mappers;

import com.example.CGI_Restaurant.domain.dtos.requests.CreateFeatureRequestDto;
import com.example.CGI_Restaurant.domain.dtos.responses.CreateFeatureResponseDto;
import com.example.CGI_Restaurant.domain.entities.Feature;
import com.example.CGI_Restaurant.domain.requests.CreateFeatureRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FeatureMapper {
    CreateFeatureRequest fromDto(CreateFeatureRequestDto dto);

    CreateFeatureResponseDto toDto(Feature feature);
}
