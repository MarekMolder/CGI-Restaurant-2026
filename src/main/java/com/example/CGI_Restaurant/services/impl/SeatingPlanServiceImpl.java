package com.example.CGI_Restaurant.services.impl;

import com.example.CGI_Restaurant.domain.entities.SeatingPlan;
import com.example.CGI_Restaurant.domain.createRequests.CreateSeatingPlanRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateSeatingPlanRequest;
import com.example.CGI_Restaurant.exceptions.notFoundExceptions.SeatingPlanNotFoundException;
import com.example.CGI_Restaurant.exceptions.updateException.SeatingPlanUpdateException;
import com.example.CGI_Restaurant.repositories.SeatingPlanRepository;
import com.example.CGI_Restaurant.services.SeatingPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Default CRUD implementation for seating plans; validates ID on update.
 */
@Service
@RequiredArgsConstructor
public class SeatingPlanServiceImpl implements SeatingPlanService {

    private final SeatingPlanRepository seatingPlanRepository;

    @Override
    public SeatingPlan create(CreateSeatingPlanRequest request) {
        SeatingPlan entity = new SeatingPlan();
        entity.setName(request.getName());
        entity.setType(request.getType());
        entity.setWidth(request.getWidth());
        entity.setHeight(request.getHeight());
        entity.setBackgroundSVG(request.getBackgroundSVG());
        entity.setActive(request.isActive());
        entity.setVersion(request.getVersion());
        return seatingPlanRepository.save(entity);
    }

    @Override
    public Page<SeatingPlan> list(Pageable pageable) {
        return seatingPlanRepository.findAll(pageable);
    }

    @Override
    public Optional<SeatingPlan> getById(UUID id) {
        return seatingPlanRepository.findById(id);
    }

    @Override
    @Transactional
    public SeatingPlan update(UUID id, UpdateSeatingPlanRequest request) {
        if (request.getId() == null || !id.equals(request.getId())) {
            throw new SeatingPlanUpdateException("Seating plan ID mismatch");
        }
        SeatingPlan entity = seatingPlanRepository.findById(id)
                .orElseThrow(() -> new SeatingPlanNotFoundException("Seating plan with ID '%s' not found".formatted(id)));
        entity.setName(request.getName());
        entity.setType(request.getType());
        entity.setWidth(request.getWidth());
        entity.setHeight(request.getHeight());
        entity.setBackgroundSVG(request.getBackgroundSVG());
        entity.setActive(request.isActive());
        entity.setVersion(request.getVersion());
        return seatingPlanRepository.save(entity);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        SeatingPlan entity = seatingPlanRepository.findById(id)
                .orElseThrow(() -> new SeatingPlanNotFoundException("Seating plan with ID '%s' not found".formatted(id)));
        seatingPlanRepository.delete(entity);
    }
}
