package com.example.CGI_Restaurant.controllers;

import com.example.CGI_Restaurant.domain.dtos.createRequests.CreateZoneRequestDto;
import com.example.CGI_Restaurant.domain.dtos.createResponses.CreateZoneResponseDto;
import com.example.CGI_Restaurant.domain.dtos.getResponses.GetZoneDetailsResponseDto;
import com.example.CGI_Restaurant.domain.dtos.listResponses.ListZoneResponseDto;
import com.example.CGI_Restaurant.domain.dtos.updateRequests.UpdateZoneRequestDto;
import com.example.CGI_Restaurant.domain.dtos.updateResponses.UpdateZoneResponseDto;
import com.example.CGI_Restaurant.domain.entities.Zone;
import com.example.CGI_Restaurant.domain.createRequests.CreateZoneRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateZoneRequest;
import com.example.CGI_Restaurant.mappers.ZoneMapper;
import com.example.CGI_Restaurant.services.ZoneService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST API for zones (e.g. indoor/outdoor areas in a seating plan). Full CRUD; write operations require ADMIN.
 */
@RestController
@RequestMapping(path = "/api/v1/zones")
@RequiredArgsConstructor
public class ZoneController {

    private final ZoneMapper zoneMapper;
    private final ZoneService zoneService;

    /** Creates a new zone. Admin only. */
    @PostMapping
    public ResponseEntity<CreateZoneResponseDto> create(@Valid @RequestBody CreateZoneRequestDto dto) {
        CreateZoneRequest request = zoneMapper.fromDto(dto);
        Zone created = zoneService.create(request);
        return new ResponseEntity<>(zoneMapper.toDto(created), HttpStatus.CREATED);
    }

    /** Returns a paginated list of zones. */
    @GetMapping
    public ResponseEntity<Page<ListZoneResponseDto>> list(Pageable pageable) {
        return ResponseEntity.ok(zoneService.list(pageable).map(zoneMapper::toListZoneResponseDto));
    }

    /** Returns a zone by ID, or 404. */
    @GetMapping("/{id}")
    public ResponseEntity<GetZoneDetailsResponseDto> getById(@PathVariable UUID id) {
        return zoneService.getById(id)
                .map(zoneMapper::toGetZoneDetailsResponseDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** Updates a zone. Admin only. */
    @PutMapping("/{id}")
    public ResponseEntity<UpdateZoneResponseDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateZoneRequestDto dto) {
        UpdateZoneRequest request = zoneMapper.fromDto(dto);
        request.setId(id);
        Zone updated = zoneService.update(id, request);
        return ResponseEntity.ok(zoneMapper.toUpdateZoneResponseDto(updated));
    }

    /** Deletes a zone by ID. Admin only. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        zoneService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
