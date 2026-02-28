package com.example.CGI_Restaurant.services;

import com.example.CGI_Restaurant.domain.dtos.listResponses.MenuItemResponseDto;
import com.example.CGI_Restaurant.domain.entities.MenuItem;
import com.example.CGI_Restaurant.domain.createRequests.CreateMenuItemRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateMenuItemRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MenuItemService {

    MenuItem create(CreateMenuItemRequest request);

    List<MenuItemResponseDto> listByRestaurantId(UUID restaurantId);

    Optional<MenuItem> getById(UUID id);

    MenuItem update(UUID id, UpdateMenuItemRequest request);

    void delete(UUID id);

    MenuItem addFromTheMealDB(UUID restaurantId, String themealdbMealId, java.math.BigDecimal priceEur);
}
