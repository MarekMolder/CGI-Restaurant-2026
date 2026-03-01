package com.example.CGI_Restaurant.services;

import com.example.CGI_Restaurant.domain.entities.Zone;
import com.example.CGI_Restaurant.domain.createRequests.CreateZoneRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateZoneRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

/**
 * CRUD service for zones (e.g. indoor/outdoor areas in a seating plan).
 */
public interface ZoneService {

    Zone create(CreateZoneRequest request);
    Page<Zone> list(Pageable pageable);
    Optional<Zone> getById(UUID id);
    Zone update(UUID id, UpdateZoneRequest request);
    void delete(UUID id);
}
