package com.example.CGI_Restaurant.services;

import com.example.CGI_Restaurant.domain.entities.Restaurant;
import com.example.CGI_Restaurant.domain.createRequests.CreateRestaurantRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateRestaurantRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface RestaurantService {
    Restaurant create(CreateRestaurantRequest request);
    Page<Restaurant> list(Pageable pageable);
    Optional<Restaurant> getById(UUID id);
    Restaurant update(UUID id, UpdateRestaurantRequest request);
    void delete(UUID id);
}
