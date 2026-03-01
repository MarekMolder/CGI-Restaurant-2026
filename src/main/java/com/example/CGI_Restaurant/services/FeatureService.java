package com.example.CGI_Restaurant.services;

import com.example.CGI_Restaurant.domain.entities.Feature;
import com.example.CGI_Restaurant.domain.createRequests.CreateFeatureRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateFeatureRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

/** CRUD service for restaurant features (e.g. outdoor seating, wheelchair access). */
public interface FeatureService {
    Feature create(CreateFeatureRequest request);
    Page<Feature> list(Pageable pageable);
    Optional<Feature> getById(UUID id);
    Feature update(UUID id, UpdateFeatureRequest request);
    void delete(UUID id);
}
