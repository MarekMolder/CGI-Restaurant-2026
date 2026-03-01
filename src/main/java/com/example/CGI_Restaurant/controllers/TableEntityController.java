package com.example.CGI_Restaurant.controllers;

import com.example.CGI_Restaurant.domain.dtos.createRequests.CreateTableEntityRequestDto;
import com.example.CGI_Restaurant.domain.dtos.createResponses.CreateTableEntityResponseDto;
import com.example.CGI_Restaurant.domain.dtos.getResponses.GetTableEntityDetailsResponseDto;
import com.example.CGI_Restaurant.domain.dtos.listResponses.ListTableEntityResponseDto;
import com.example.CGI_Restaurant.domain.dtos.listResponses.TableAvailabilityItemDto;
import com.example.CGI_Restaurant.domain.dtos.updateRequests.UpdateTableEntityRequestDto;
import com.example.CGI_Restaurant.domain.dtos.updateResponses.UpdateTableEntityResponseDto;
import com.example.CGI_Restaurant.domain.entities.TableEntity;
import com.example.CGI_Restaurant.domain.createRequests.CreateTableEntityRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateTableEntityRequest;
import com.example.CGI_Restaurant.mappers.TableEntityMapper;
import com.example.CGI_Restaurant.services.TableEntityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * REST API for table entities (physical tables in a zone). Supports CRUD and availability search.
 * Create/update/delete are admin-only. GET /available returns tables free in a time slot for a party size.
 */
@RestController
@RequestMapping(path = "/api/v1/table-entities")
@RequiredArgsConstructor
public class TableEntityController {

    private final TableEntityMapper tableEntityMapper;
    private final TableEntityService tableEntityService;

    /** Creates a new table entity, optionally with adjacent tables. Admin only. */
    @PostMapping
    public ResponseEntity<CreateTableEntityResponseDto> create(@Valid @RequestBody CreateTableEntityRequestDto dto) {
        CreateTableEntityRequest request = tableEntityMapper.fromDto(dto);
        TableEntity created = tableEntityService.create(request);
        return new ResponseEntity<>(tableEntityMapper.toDto(created), HttpStatus.CREATED);
    }

    /** Lists table entities, optionally filtered by search query {@code q}. */
    @GetMapping
    public ResponseEntity<Page<ListTableEntityResponseDto>> list(
            @RequestParam(required = false) String q,
            Pageable pageable) {

        Page<TableEntity> tableEntities;
        if(null != q && !q.trim().isEmpty()) {
            tableEntities = tableEntityService.searchAvailableTables(q, pageable);
        } else {
            tableEntities = tableEntityService.list(pageable);
        }

        return ResponseEntity.ok(tableEntities.map(tableEntityMapper::toListTableEntityResponseDto));
    }

    /** Returns a single table entity by ID, or 404. */
    @GetMapping("/{id}")
    public ResponseEntity<GetTableEntityDetailsResponseDto> getById(@PathVariable UUID id) {
        return tableEntityService.getById(id)
                .map(tableEntityMapper::toGetTableEntityDetailsResponseDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** Updates a table entity. Admin only. */
    @PutMapping("/{id}")
    public ResponseEntity<UpdateTableEntityResponseDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTableEntityRequestDto dto) {
        UpdateTableEntityRequest request = tableEntityMapper.fromDto(dto);
        request.setId(id);
        TableEntity updated = tableEntityService.update(id, request);
        return ResponseEntity.ok(tableEntityMapper.toUpdateTableEntityResponseDto(updated));
    }

    /** Deletes a table entity by ID. Admin only. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        tableEntityService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /** Returns tables available in the given time range for the party size, optionally filtered by zone or seating plan and preferred features. */
    @GetMapping("/available")
    public ResponseEntity<List<TableAvailabilityItemDto>> getAvailableTables(
            @RequestParam(required = false) UUID seatingPlanId,
            @RequestParam(required = false) UUID zoneId,
            @RequestParam int partySize,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startAt,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endAt,
            @RequestParam(required = false) List<UUID> preferredFeatureIds) {
        if (seatingPlanId == null && zoneId == null) {
            return ResponseEntity.badRequest().build();
        }
        List<TableAvailabilityItemDto> list = tableEntityService.findTablesWithAvailability(
                seatingPlanId, zoneId, partySize, startAt, endAt, preferredFeatureIds);
        return ResponseEntity.ok(list);
    }
}
