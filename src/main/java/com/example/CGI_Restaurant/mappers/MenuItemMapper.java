package com.example.CGI_Restaurant.mappers;

import com.example.CGI_Restaurant.domain.dtos.listResponses.MenuItemResponseDto;
import com.example.CGI_Restaurant.domain.dtos.createRequests.CreateMenuItemRequestDto;
import com.example.CGI_Restaurant.domain.dtos.updateRequests.UpdateMenuItemRequestDto;
import com.example.CGI_Restaurant.domain.entities.MenuItem;
import com.example.CGI_Restaurant.domain.createRequests.CreateMenuItemRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateMenuItemRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MenuItemMapper {

    MenuItemResponseDto toMenuItemResponseDto(MenuItem entity);

    CreateMenuItemRequest fromDto(CreateMenuItemRequestDto dto);

    UpdateMenuItemRequest fromDto(UpdateMenuItemRequestDto dto);
}
