package com.example.CGI_Restaurant.services.impl;

import com.example.CGI_Restaurant.domain.dtos.listResponses.TableAvailabilityItemDto;
import com.example.CGI_Restaurant.domain.entities.TableEntity;
import com.example.CGI_Restaurant.domain.createRequests.CreateTableEntityRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateTableEntityRequest;
import com.example.CGI_Restaurant.exceptions.notFoundExceptions.TableEntityNotFoundException;
import com.example.CGI_Restaurant.exceptions.updateException.TableEntityUpdateException;
import com.example.CGI_Restaurant.mappers.TableEntityMapper;
import com.example.CGI_Restaurant.repositories.BookingTableRepository;
import com.example.CGI_Restaurant.repositories.TableEntityRepository;
import com.example.CGI_Restaurant.repositories.ZoneRepository;
import com.example.CGI_Restaurant.services.TableEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TableEntityServiceImpl implements TableEntityService {

    /** Maksimaalne lubatud tühjade kohtade arv lauas (nt 2 → 8-kohalist lauda ei pakuta 2 inimesele). */
    public static final int MAX_EMPTY_SEATS = 2;

    /** Boonus soovituse skoorile iga sobitunud eelistuse (feature) eest. */
    private static final int FEATURE_MATCH_BONUS = 20;

    private final TableEntityRepository tableEntityRepository;
    private final BookingTableRepository bookingTableRepository;
    private final ZoneRepository zoneRepository;
    private final TableEntityMapper tableEntityMapper;

    @Override
    public TableEntity create(CreateTableEntityRequest request) {
        TableEntity entity = new TableEntity();
        entity.setLabel(request.getLabel());
        entity.setCapacity(request.getCapacity());
        entity.setMinPartySize(request.getMinPartySize());
        entity.setShape(request.getShape());
        entity.setX(request.getX());
        entity.setY(request.getY());
        entity.setWidth(request.getWidth());
        entity.setHeight(request.getHeight());
        entity.setRotationDegree(request.getRotationDegree());
        entity.setActive(request.isActive());
        return tableEntityRepository.save(entity);
    }

    @Override
    public Page<TableEntity> list(Pageable pageable) {
        return tableEntityRepository.findAll(pageable);
    }

    @Override
    public Optional<TableEntity> getById(UUID id) {
        return tableEntityRepository.findById(id);
    }

    @Override
    @Transactional
    public TableEntity update(UUID id, UpdateTableEntityRequest request) {
        if (request.getId() == null || !id.equals(request.getId())) {
            throw new TableEntityUpdateException("Table entity ID mismatch");
        }
        TableEntity entity = tableEntityRepository.findById(id)
                .orElseThrow(() -> new TableEntityNotFoundException("Table entity with ID '%s' not found".formatted(id)));
        entity.setLabel(request.getLabel());
        entity.setCapacity(request.getCapacity());
        entity.setMinPartySize(request.getMinPartySize());
        entity.setShape(request.getShape());
        entity.setX(request.getX());
        entity.setY(request.getY());
        entity.setWidth(request.getWidth());
        entity.setHeight(request.getHeight());
        entity.setRotationDegree(request.getRotationDegree());
        entity.setActive(request.isActive());
        return tableEntityRepository.save(entity);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        TableEntity entity = tableEntityRepository.findById(id)
                .orElseThrow(() -> new TableEntityNotFoundException("Table entity with ID '%s' not found".formatted(id)));
        tableEntityRepository.delete(entity);
    }

    @Override
    public Page<TableEntity> searchAvailableTables(String query, Pageable pageable) {
        return tableEntityRepository.searchTableEntities(query, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TableAvailabilityItemDto> findTablesWithAvailability(UUID seatingPlanId, UUID zoneId, int partySize,
                                                                     LocalDateTime startAt, LocalDateTime endAt,
                                                                     List<UUID> preferredFeatureIds) {
        if (seatingPlanId == null && zoneId == null) {
            throw new IllegalArgumentException("Either seatingPlanId or zoneId must be provided");
        }
        Page<TableEntity> tablePage = zoneId != null
                ? tableEntityRepository.findByZoneIdAndActiveTrue(zoneId, Pageable.unpaged())
                : tableEntityRepository.findBySeatingPlanIdAndActiveTrue(seatingPlanId, Pageable.unpaged());
        List<TableEntity> tables = tablePage.getContent().stream()
                .filter(t -> t.getCapacity() >= partySize && t.getMinPartySize() <= partySize)
                .filter(t -> (t.getCapacity() - partySize) <= MAX_EMPTY_SEATS)
                .toList();

        Set<UUID> preferredIds = preferredFeatureIds != null && !preferredFeatureIds.isEmpty()
                ? new HashSet<>(preferredFeatureIds) : Set.of();
        Map<UUID, Set<UUID>> zoneIdToFeatureIds = new HashMap<>();
        if (!preferredIds.isEmpty() && !tables.isEmpty()) {
            List<UUID> zoneIds = tables.stream()
                    .map(t -> t.getZone() != null ? t.getZone().getId() : null)
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();
            if (!zoneIds.isEmpty()) {
                zoneRepository.findByIdInWithFeatures(zoneIds).forEach(z -> {
                    Set<UUID> fIds = z.getFeatures().stream().map(com.example.CGI_Restaurant.domain.entities.Feature::getId).collect(Collectors.toSet());
                    zoneIdToFeatureIds.put(z.getId(), fIds);
                });
            }
        }

        Set<UUID> bookedTableIds = bookingTableRepository.findTableEntityIdsBookedBetween(startAt, endAt).stream()
                .collect(Collectors.toSet());

        return tables.stream()
                .map(t -> {
                    boolean available = !bookedTableIds.contains(t.getId());
                    Integer score = null;
                    if (available) {
                        int baseScore = 100 - Math.max(0, t.getCapacity() - partySize);
                        int featureBonus = 0;
                        if (t.getZone() != null && zoneIdToFeatureIds.containsKey(t.getZone().getId())) {
                            for (UUID fid : preferredIds) {
                                if (zoneIdToFeatureIds.get(t.getZone().getId()).contains(fid)) {
                                    featureBonus += FEATURE_MATCH_BONUS;
                                }
                            }
                        }
                        score = baseScore + featureBonus;
                    }
                    return tableEntityMapper.toTableAvailabilityItemDto(t, available, score);
                })
                .sorted((a, b) -> {
                    if (a.isAvailable() != b.isAvailable()) return a.isAvailable() ? -1 : 1;
                    Integer sa = a.getRecommendationScore() != null ? a.getRecommendationScore() : 0;
                    Integer sb = b.getRecommendationScore() != null ? b.getRecommendationScore() : 0;
                    return Integer.compare(sb, sa);
                })
                .toList();
    }
}
