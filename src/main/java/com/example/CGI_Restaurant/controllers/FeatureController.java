package com.example.CGI_Restaurant.controllers;

import com.example.CGI_Restaurant.domain.dtos.createRequests.CreateFeatureRequestDto;
import com.example.CGI_Restaurant.domain.dtos.createResponses.CreateFeatureResponseDto;
import com.example.CGI_Restaurant.domain.dtos.getResponses.GetFeatureDetailsResponseDto;
import com.example.CGI_Restaurant.domain.dtos.listResponses.ListFeatureResponseDto;
import com.example.CGI_Restaurant.domain.dtos.updateRequests.UpdateFeatureRequestDto;
import com.example.CGI_Restaurant.domain.dtos.updateResponses.UpdateFeatureResponseDto;
import com.example.CGI_Restaurant.domain.entities.Feature;
import com.example.CGI_Restaurant.domain.createRequests.CreateFeatureRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateFeatureRequest;
import com.example.CGI_Restaurant.mappers.FeatureMapper;
import com.example.CGI_Restaurant.services.FeatureService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/features")
@RequiredArgsConstructor
public class FeatureController {

    private final FeatureMapper featureMapper;
    private final FeatureService featureService;

    @PostMapping
    public ResponseEntity<CreateFeatureResponseDto> create(@Valid @RequestBody CreateFeatureRequestDto dto) {
        CreateFeatureRequest request = featureMapper.fromDto(dto);
        Feature created = featureService.create(request);
        return new ResponseEntity<>(featureMapper.toDto(created), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<ListFeatureResponseDto>> list(Pageable pageable) {
        return ResponseEntity.ok(featureService.list(pageable).map(featureMapper::toListFeatureResponseDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetFeatureDetailsResponseDto> getById(@PathVariable UUID id) {
        return featureService.getById(id)
                .map(featureMapper::toGetFeatureDetailsResponseDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdateFeatureResponseDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateFeatureRequestDto dto) {
        UpdateFeatureRequest request = featureMapper.fromDto(dto);
        request.setId(id);
        Feature updated = featureService.update(id, request);
        return ResponseEntity.ok(featureMapper.toUpdateFeatureResponseDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        featureService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
