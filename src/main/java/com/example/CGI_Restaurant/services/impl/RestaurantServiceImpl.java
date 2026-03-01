package com.example.CGI_Restaurant.services.impl;

import com.example.CGI_Restaurant.domain.entities.Restaurant;
import com.example.CGI_Restaurant.domain.createRequests.CreateRestaurantRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateRestaurantRequest;
import com.example.CGI_Restaurant.exceptions.notFoundExceptions.RestaurantNotFoundException;
import com.example.CGI_Restaurant.exceptions.updateException.RestaurantUpdateException;
import com.example.CGI_Restaurant.repositories.RestaurantRepository;
import com.example.CGI_Restaurant.services.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Default CRUD implementation for restaurants; validates ID on update.
 */
@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;

    @Override
    public Restaurant create(CreateRestaurantRequest request) {
        Restaurant entity = new Restaurant();
        entity.setName(request.getName());
        entity.setTimezone(request.getTimezone());
        entity.setEmail(request.getEmail());
        entity.setPhone(request.getPhone());
        entity.setAddress(request.getAddress());
        return restaurantRepository.save(entity);
    }

    @Override
    public Page<Restaurant> list(Pageable pageable) {
        return restaurantRepository.findAll(pageable);
    }

    @Override
    public Optional<Restaurant> getById(UUID id) {
        return restaurantRepository.findById(id);
    }

    @Override
    @Transactional
    public Restaurant update(UUID id, UpdateRestaurantRequest request) {
        if (request.getId() == null || !id.equals(request.getId())) {
            throw new RestaurantUpdateException("Restaurant ID mismatch");
        }
        Restaurant entity = restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant with ID '%s' not found".formatted(id)));
        entity.setName(request.getName());
        entity.setTimezone(request.getTimezone());
        entity.setEmail(request.getEmail());
        entity.setPhone(request.getPhone());
        entity.setAddress(request.getAddress());
        return restaurantRepository.save(entity);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Restaurant entity = restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant with ID '%s' not found".formatted(id)));
        restaurantRepository.delete(entity);
    }
}
