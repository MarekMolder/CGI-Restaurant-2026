package com.example.CGI_Restaurant.mappers;

import com.example.CGI_Restaurant.domain.dtos.requests.CreateRestaurantRequestDto;
import com.example.CGI_Restaurant.domain.dtos.responses.CreateRestaurantResponseDto;
import com.example.CGI_Restaurant.domain.entities.Restaurant;
import com.example.CGI_Restaurant.domain.requests.CreateRestaurantRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RestaurantMapper {
    CreateRestaurantRequest fromDto(CreateRestaurantRequestDto dto);

    CreateRestaurantResponseDto toDto(Restaurant restaurant);
}
