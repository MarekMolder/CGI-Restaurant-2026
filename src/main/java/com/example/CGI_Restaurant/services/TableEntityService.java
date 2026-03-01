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
import java.util.Set;
import java.util.UUID;

/**
 * Service for table entities: CRUD, search by label, availability in a time slot (single and combined tables),
 * and validation that multiple tables are adjacent when booking.
 */
public interface TableEntityService {

    TableEntity create(CreateTableEntityRequest request);
    Page<TableEntity> list(Pageable pageable);
    Optional<TableEntity> getById(UUID id);
    TableEntity update(UUID id, UpdateTableEntityRequest request);
    void delete(UUID id);

    /** Search tables by label (e.g. for admin UI). */
    Page<TableEntity> searchAvailableTables(String query, Pageable pageable);

    /** Returns tables available in the time range for the party size, with optional feature preference scoring; by zone or seating plan. */
    List<TableAvailabilityItemDto> findTablesWithAvailability(UUID seatingPlanId, UUID zoneId, int partySize,
                                                             LocalDateTime startAt, LocalDateTime endAt,
                                                             List<UUID> preferredFeatureIds);

    /** Ensures all given table IDs exist and form a connected set (adjacent). Throws if not. */
    void validateTablesAdjacent(Set<UUID> tableIds);
}
