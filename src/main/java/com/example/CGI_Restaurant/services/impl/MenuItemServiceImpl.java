package com.example.CGI_Restaurant.services.impl;

import com.example.CGI_Restaurant.domain.dtos.listResponses.MenuItemResponseDto;
import com.example.CGI_Restaurant.domain.entities.MenuItem;
import com.example.CGI_Restaurant.domain.entities.Restaurant;
import com.example.CGI_Restaurant.domain.createRequests.CreateMenuItemRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateMenuItemRequest;
import com.example.CGI_Restaurant.exceptions.notFoundExceptions.RestaurantNotFoundException;
import com.example.CGI_Restaurant.mappers.MenuItemMapper;
import com.example.CGI_Restaurant.repositories.MenuItemRepository;
import com.example.CGI_Restaurant.repositories.RestaurantRepository;
import com.example.CGI_Restaurant.services.MenuItemService;
import com.example.CGI_Restaurant.services.TheMealDBService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * CRUD for menu items and import from TheMealDB; maps to response DTOs for list by restaurant.
 */
@Service
@RequiredArgsConstructor
public class MenuItemServiceImpl implements MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuItemMapper menuItemMapper;
    private final TheMealDBService theMealDBService;

    @Override
    @Transactional
    public MenuItem create(CreateMenuItemRequest request) {
        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant not found"));
        MenuItem entity = new MenuItem();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setPriceEur(request.getPriceEur());
        entity.setCategory(request.getCategory());
        entity.setImageUrl(request.getImageUrl());
        entity.setRestaurant(restaurant);
        return menuItemRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItemResponseDto> listByRestaurantId(UUID restaurantId) {
        return menuItemRepository.findByRestaurantIdOrderByCategoryAscNameAsc(restaurantId).stream()
                .map(menuItemMapper::toMenuItemResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MenuItem> getById(UUID id) {
        return menuItemRepository.findById(id);
    }

    @Override
    @Transactional
    public MenuItem update(UUID id, UpdateMenuItemRequest request) {
        MenuItem entity = menuItemRepository.findById(id)
                .orElseThrow(() -> new com.example.CGI_Restaurant.exceptions.notFoundExceptions.MenuItemNotFoundException("Menu item not found"));
        if (request.getName() != null) entity.setName(request.getName());
        if (request.getDescription() != null) entity.setDescription(request.getDescription());
        if (request.getPriceEur() != null) entity.setPriceEur(request.getPriceEur());
        if (request.getCategory() != null) entity.setCategory(request.getCategory());
        if (request.getImageUrl() != null) entity.setImageUrl(request.getImageUrl());
        return menuItemRepository.save(entity);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        MenuItem entity = menuItemRepository.findById(id)
                .orElseThrow(() -> new com.example.CGI_Restaurant.exceptions.notFoundExceptions.MenuItemNotFoundException("Menu item not found"));
        menuItemRepository.delete(entity);
    }

    @Override
    @Transactional
    public MenuItem addFromTheMealDB(UUID restaurantId, String themealdbMealId, java.math.BigDecimal priceEur) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant not found"));
        return theMealDBService.importMealAsMenuItem(restaurant, themealdbMealId, priceEur);
    }
}
