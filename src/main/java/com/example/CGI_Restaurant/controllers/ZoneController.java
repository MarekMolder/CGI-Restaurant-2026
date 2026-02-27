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

@RestController
@RequestMapping(path = "/api/v1/zones")
@RequiredArgsConstructor
public class ZoneController {

    private final ZoneMapper zoneMapper;
    private final ZoneService zoneService;

    @PostMapping
    public ResponseEntity<CreateZoneResponseDto> create(@Valid @RequestBody CreateZoneRequestDto dto) {
        CreateZoneRequest request = zoneMapper.fromDto(dto);
        Zone created = zoneService.create(request);
        return new ResponseEntity<>(zoneMapper.toDto(created), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<ListZoneResponseDto>> list(Pageable pageable) {
        return ResponseEntity.ok(zoneService.list(pageable).map(zoneMapper::toListZoneResponseDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetZoneDetailsResponseDto> getById(@PathVariable UUID id) {
        return zoneService.getById(id)
                .map(zoneMapper::toGetZoneDetailsResponseDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdateZoneResponseDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateZoneRequestDto dto) {
        UpdateZoneRequest request = zoneMapper.fromDto(dto);
        request.setId(id);
        Zone updated = zoneService.update(id, request);
        return ResponseEntity.ok(zoneMapper.toUpdateZoneResponseDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        zoneService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
