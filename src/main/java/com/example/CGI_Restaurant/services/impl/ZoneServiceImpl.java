package com.example.CGI_Restaurant.services.impl;

import com.example.CGI_Restaurant.domain.entities.Zone;
import com.example.CGI_Restaurant.domain.createRequests.CreateZoneRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateZoneRequest;
import com.example.CGI_Restaurant.exceptions.notFoundExceptions.ZoneNotFoundException;
import com.example.CGI_Restaurant.exceptions.updateException.ZoneUpdateException;
import com.example.CGI_Restaurant.repositories.ZoneRepository;
import com.example.CGI_Restaurant.services.ZoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Default CRUD implementation for zones; validates ID on update.
 */
@Service
@RequiredArgsConstructor
public class ZoneServiceImpl implements ZoneService {

    private final ZoneRepository zoneRepository;

    @Override
    public Zone create(CreateZoneRequest request) {
        Zone entity = new Zone();
        entity.setName(request.getName());
        entity.setType(request.getType());
        entity.setColor(request.getColor());
        return zoneRepository.save(entity);
    }

    @Override
    public Page<Zone> list(Pageable pageable) {
        return zoneRepository.findAll(pageable);
    }

    @Override
    public Optional<Zone> getById(UUID id) {
        return zoneRepository.findById(id);
    }

    @Override
    @Transactional
    public Zone update(UUID id, UpdateZoneRequest request) {
        if (request.getId() == null || !id.equals(request.getId())) {
            throw new ZoneUpdateException("Zone ID mismatch");
        }
        Zone entity = zoneRepository.findById(id)
                .orElseThrow(() -> new ZoneNotFoundException("Zone with ID '%s' not found".formatted(id)));
        entity.setName(request.getName());
        entity.setType(request.getType());
        entity.setColor(request.getColor());
        return zoneRepository.save(entity);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Zone entity = zoneRepository.findById(id)
                .orElseThrow(() -> new ZoneNotFoundException("Zone with ID '%s' not found".formatted(id)));
        zoneRepository.delete(entity);
    }
}
