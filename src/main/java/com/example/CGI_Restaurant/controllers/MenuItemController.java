package com.example.CGI_Restaurant.controllers;

import com.example.CGI_Restaurant.domain.dtos.createRequests.AddMenuItemFromTheMealDBRequestDto;
import com.example.CGI_Restaurant.domain.dtos.createRequests.CreateMenuItemRequestDto;
import com.example.CGI_Restaurant.domain.dtos.listResponses.MenuItemResponseDto;
import com.example.CGI_Restaurant.domain.dtos.updateRequests.UpdateMenuItemRequestDto;
import com.example.CGI_Restaurant.domain.entities.MenuItem;
import com.example.CGI_Restaurant.domain.createRequests.CreateMenuItemRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateMenuItemRequest;
import com.example.CGI_Restaurant.mappers.MenuItemMapper;
import com.example.CGI_Restaurant.services.MenuItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/restaurants/{restaurantId}/menu")
@RequiredArgsConstructor
public class MenuItemController {

    private final MenuItemService menuItemService;
    private final MenuItemMapper menuItemMapper;

    @GetMapping
    public ResponseEntity<List<MenuItemResponseDto>> list(@PathVariable UUID restaurantId) {
        List<MenuItemResponseDto> items = menuItemService.listByRestaurantId(restaurantId);
        return ResponseEntity.ok(items);
    }

    @PostMapping
    public ResponseEntity<MenuItemResponseDto> create(
            @PathVariable UUID restaurantId,
            @Valid @RequestBody CreateMenuItemRequestDto dto) {
        CreateMenuItemRequest request = menuItemMapper.fromDto(dto);
        request.setRestaurantId(restaurantId);
        MenuItem created = menuItemService.create(request);
        return new ResponseEntity<>(menuItemMapper.toMenuItemResponseDto(created), HttpStatus.CREATED);
    }

    @PostMapping("/from-themealdb")
    public ResponseEntity<MenuItemResponseDto> addFromTheMealDB(
            @PathVariable UUID restaurantId,
            @Valid @RequestBody AddMenuItemFromTheMealDBRequestDto dto) {
        MenuItem created = menuItemService.addFromTheMealDB(restaurantId, dto.getMealId(), dto.getPriceEur());
        return new ResponseEntity<>(menuItemMapper.toMenuItemResponseDto(created), HttpStatus.CREATED);
    }

    @PutMapping("/{menuItemId}")
    public ResponseEntity<MenuItemResponseDto> update(
            @PathVariable UUID restaurantId,
            @PathVariable UUID menuItemId,
            @Valid @RequestBody UpdateMenuItemRequestDto dto) {
        UpdateMenuItemRequest request = menuItemMapper.fromDto(dto);
        request.setId(menuItemId);
        MenuItem updated = menuItemService.update(menuItemId, request);
        return ResponseEntity.ok(menuItemMapper.toMenuItemResponseDto(updated));
    }

    @DeleteMapping("/{menuItemId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID restaurantId,
            @PathVariable UUID menuItemId) {
        menuItemService.delete(menuItemId);
        return ResponseEntity.noContent().build();
    }
}
