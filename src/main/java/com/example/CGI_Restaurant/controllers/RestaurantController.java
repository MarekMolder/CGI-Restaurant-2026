package com.example.CGI_Restaurant.controllers;

import com.example.CGI_Restaurant.domain.dtos.createRequests.CreateRestaurantRequestDto;
import com.example.CGI_Restaurant.domain.dtos.createResponses.CreateRestaurantResponseDto;
import com.example.CGI_Restaurant.domain.dtos.getResponses.GetRestaurantDetailsResponseDto;
import com.example.CGI_Restaurant.domain.dtos.listResponses.ListRestaurantResponseDto;
import com.example.CGI_Restaurant.domain.dtos.updateRequests.UpdateRestaurantRequestDto;
import com.example.CGI_Restaurant.domain.dtos.updateResponses.UpdateRestaurantResponseDto;
import com.example.CGI_Restaurant.domain.entities.Restaurant;
import com.example.CGI_Restaurant.domain.createRequests.CreateRestaurantRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateRestaurantRequest;
import com.example.CGI_Restaurant.mappers.RestaurantMapper;
import com.example.CGI_Restaurant.services.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantMapper restaurantMapper;
    private final RestaurantService restaurantService;

    @PostMapping
    public ResponseEntity<CreateRestaurantResponseDto> create(@Valid @RequestBody CreateRestaurantRequestDto dto) {
        CreateRestaurantRequest request = restaurantMapper.fromDto(dto);
        Restaurant created = restaurantService.create(request);
        return new ResponseEntity<>(restaurantMapper.toDto(created), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<ListRestaurantResponseDto>> list(Pageable pageable) {
        return ResponseEntity.ok(restaurantService.list(pageable).map(restaurantMapper::toListRestaurantResponseDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetRestaurantDetailsResponseDto> getById(@PathVariable UUID id) {
        return restaurantService.getById(id)
                .map(restaurantMapper::toGetRestaurantDetailsResponseDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdateRestaurantResponseDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateRestaurantRequestDto dto) {
        UpdateRestaurantRequest request = restaurantMapper.fromDto(dto);
        request.setId(id);
        Restaurant updated = restaurantService.update(id, request);
        return ResponseEntity.ok(restaurantMapper.toUpdateRestaurantResponseDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        restaurantService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
