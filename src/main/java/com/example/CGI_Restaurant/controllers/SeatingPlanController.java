package com.example.CGI_Restaurant.controllers;

import com.example.CGI_Restaurant.domain.dtos.createRequests.CreateSeatingPlanRequestDto;
import com.example.CGI_Restaurant.domain.dtos.createResponses.CreateSeatingPlanResponseDto;
import com.example.CGI_Restaurant.domain.dtos.getResponses.GetSeatingPlanDetailsResponseDto;
import com.example.CGI_Restaurant.domain.dtos.listResponses.ListSeatingPlanResponseDto;
import com.example.CGI_Restaurant.domain.dtos.updateRequests.UpdateSeatingPlanRequestDto;
import com.example.CGI_Restaurant.domain.dtos.updateResponses.UpdateSeatingPlanResponseDto;
import com.example.CGI_Restaurant.domain.entities.SeatingPlan;
import com.example.CGI_Restaurant.domain.createRequests.CreateSeatingPlanRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateSeatingPlanRequest;
import com.example.CGI_Restaurant.mappers.SeatingPlanMapper;
import com.example.CGI_Restaurant.services.SeatingPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/seating-plans")
@RequiredArgsConstructor
public class SeatingPlanController {

    private final SeatingPlanMapper seatingPlanMapper;
    private final SeatingPlanService seatingPlanService;

    @PostMapping
    public ResponseEntity<CreateSeatingPlanResponseDto> create(@Valid @RequestBody CreateSeatingPlanRequestDto dto) {
        CreateSeatingPlanRequest request = seatingPlanMapper.fromDto(dto);
        SeatingPlan created = seatingPlanService.create(request);
        return new ResponseEntity<>(seatingPlanMapper.toDto(created), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<ListSeatingPlanResponseDto>> list(Pageable pageable) {
        return ResponseEntity.ok(seatingPlanService.list(pageable).map(seatingPlanMapper::toListSeatingPlanResponseDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetSeatingPlanDetailsResponseDto> getById(@PathVariable UUID id) {
        return seatingPlanService.getById(id)
                .map(seatingPlanMapper::toGetSeatingPlanDetailsResponseDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdateSeatingPlanResponseDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateSeatingPlanRequestDto dto) {
        UpdateSeatingPlanRequest request = seatingPlanMapper.fromDto(dto);
        request.setId(id);
        SeatingPlan updated = seatingPlanService.update(id, request);
        return ResponseEntity.ok(seatingPlanMapper.toUpdateSeatingPlanResponseDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        seatingPlanService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
