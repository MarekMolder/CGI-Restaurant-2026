package com.example.CGI_Restaurant.services;

import com.example.CGI_Restaurant.domain.dtos.listResponses.TableAvailabilityItemDto;
import com.example.CGI_Restaurant.domain.entities.TableEntity;
import com.example.CGI_Restaurant.domain.createRequests.CreateTableEntityRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateTableEntityRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TableEntityService {
    TableEntity create(CreateTableEntityRequest request);
    Page<TableEntity> list(Pageable pageable);
    Optional<TableEntity> getById(UUID id);
    TableEntity update(UUID id, UpdateTableEntityRequest request);
    void delete(UUID id);

    Page<TableEntity> searchAvailableTables(String query, Pageable pageable);

    List<TableAvailabilityItemDto> findTablesWithAvailability(UUID seatingPlanId, UUID zoneId, int partySize,
                                                             LocalDateTime startAt, LocalDateTime endAt,
                                                             List<UUID> preferredFeatureIds);
}
