package com.example.CGI_Restaurant.services;

import com.example.CGI_Restaurant.domain.dtos.listResponses.MenuItemResponseDto;
import com.example.CGI_Restaurant.domain.entities.MenuItem;
import com.example.CGI_Restaurant.domain.createRequests.CreateMenuItemRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateMenuItemRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for restaurant menu items: CRUD and import from TheMealDB.
 */
public interface MenuItemService {

    /** Creates a menu item for the given restaurant. */
    MenuItem create(CreateMenuItemRequest request);

    /** Returns all menu items for a restaurant as response DTOs. */
    List<MenuItemResponseDto> listByRestaurantId(UUID restaurantId);

    Optional<MenuItem> getById(UUID id);

    /** Updates a menu item by ID. */
    MenuItem update(UUID id, UpdateMenuItemRequest request);

    void delete(UUID id);

    /** Imports a meal from TheMealDB as a new menu item for the restaurant. */
    MenuItem addFromTheMealDB(UUID restaurantId, String themealdbMealId, java.math.BigDecimal priceEur);
}
