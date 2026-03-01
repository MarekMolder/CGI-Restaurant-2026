package com.example.CGI_Restaurant.services.impl;

import com.example.CGI_Restaurant.domain.entities.Feature;
import com.example.CGI_Restaurant.domain.createRequests.CreateFeatureRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateFeatureRequest;
import com.example.CGI_Restaurant.exceptions.notFoundExceptions.FeatureNotFoundException;
import com.example.CGI_Restaurant.exceptions.updateException.FeatureUpdateException;
import com.example.CGI_Restaurant.repositories.FeatureRepository;
import com.example.CGI_Restaurant.services.FeatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Default CRUD implementation for features; validates ID on update.
 */
@Service
@RequiredArgsConstructor
public class FeatureServiceImpl implements FeatureService {

    private final FeatureRepository featureRepository;

    @Override
    public Feature create(CreateFeatureRequest request) {
        Feature entity = new Feature();
        entity.setCode(request.getCode());
        entity.setName(request.getName());
        return featureRepository.save(entity);
    }

    @Override
    public Page<Feature> list(Pageable pageable) {
        return featureRepository.findAll(pageable);
    }

    @Override
    public Optional<Feature> getById(UUID id) {
        return featureRepository.findById(id);
    }

    @Override
    @Transactional
    public Feature update(UUID id, UpdateFeatureRequest request) {
        if (request.getId() == null || !id.equals(request.getId())) {
            throw new FeatureUpdateException("Feature ID mismatch");
        }
        Feature entity = featureRepository.findById(id)
                .orElseThrow(() -> new FeatureNotFoundException("Feature with ID '%s' not found".formatted(id)));
        entity.setCode(request.getCode());
        entity.setName(request.getName());
        return featureRepository.save(entity);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Feature entity = featureRepository.findById(id)
                .orElseThrow(() -> new FeatureNotFoundException("Feature with ID '%s' not found".formatted(id)));
        featureRepository.delete(entity);
    }
}
