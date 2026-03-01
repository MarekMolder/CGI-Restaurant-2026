package com.example.CGI_Restaurant.services;

import com.example.CGI_Restaurant.domain.entities.SeatingPlan;
import com.example.CGI_Restaurant.domain.createRequests.CreateSeatingPlanRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateSeatingPlanRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

/**
 * CRUD service for seating plans (layout of zones and tables per restaurant).
 */
public interface SeatingPlanService {

    SeatingPlan create(CreateSeatingPlanRequest request);
    Page<SeatingPlan> list(Pageable pageable);
    Optional<SeatingPlan> getById(UUID id);
    SeatingPlan update(UUID id, UpdateSeatingPlanRequest request);
    void delete(UUID id);
}
