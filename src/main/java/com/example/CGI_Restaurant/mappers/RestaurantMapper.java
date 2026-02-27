package com.example.CGI_Restaurant.mappers;

import com.example.CGI_Restaurant.domain.dtos.getResponses.GetRestaurantDetailsResponseDto;
import com.example.CGI_Restaurant.domain.dtos.listResponses.ListRestaurantResponseDto;
import com.example.CGI_Restaurant.domain.dtos.createRequests.CreateRestaurantRequestDto;
import com.example.CGI_Restaurant.domain.dtos.createResponses.CreateRestaurantResponseDto;
import com.example.CGI_Restaurant.domain.dtos.updateRequests.UpdateRestaurantRequestDto;
import com.example.CGI_Restaurant.domain.dtos.updateResponses.UpdateRestaurantResponseDto;
import com.example.CGI_Restaurant.domain.entities.Restaurant;
import com.example.CGI_Restaurant.domain.createRequests.CreateRestaurantRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateRestaurantRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = SeatingPlanMapper.class)
public interface RestaurantMapper {
    CreateRestaurantRequest fromDto(CreateRestaurantRequestDto dto);

    CreateRestaurantResponseDto toDto(Restaurant restaurant);

    ListRestaurantResponseDto toListRestaurantResponseDto(Restaurant restaurant);

    GetRestaurantDetailsResponseDto toGetRestaurantDetailsResponseDto(Restaurant restaurant);

    UpdateRestaurantRequest fromDto(UpdateRestaurantRequestDto dto);

    UpdateRestaurantResponseDto toUpdateRestaurantResponseDto(Restaurant restaurant);
}
