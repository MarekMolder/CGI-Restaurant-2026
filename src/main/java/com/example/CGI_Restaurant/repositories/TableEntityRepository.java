package com.example.CGI_Restaurant.repositories;

import com.example.CGI_Restaurant.domain.entities.TableEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TableEntityRepository extends JpaRepository<TableEntity, UUID> {

    @Query("SELECT t FROM TableEntity t WHERE t.active = true AND LOWER(t.label) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<TableEntity> searchTableEntities(@Param("searchTerm") String searchTerm, Pageable pageable);

    Page<TableEntity> findByZoneIdAndActiveTrue(UUID zoneId, Pageable pageable);

    Page<TableEntity> findBySeatingPlanIdAndActiveTrue(UUID seatingPlanId, Pageable pageable);

    @Query("SELECT DISTINCT t FROM TableEntity t LEFT JOIN FETCH t.adjacentTables WHERE t.zone.id = :zoneId AND t.active = true")
    List<TableEntity> findByZoneIdAndActiveTrueWithAdjacent(@Param("zoneId") UUID zoneId);

    @Query("SELECT DISTINCT t FROM TableEntity t LEFT JOIN FETCH t.adjacentTables WHERE t.seatingPlan.id = :seatingPlanId AND t.active = true")
    List<TableEntity> findBySeatingPlanIdAndActiveTrueWithAdjacent(@Param("seatingPlanId") UUID seatingPlanId);

    @Query("SELECT DISTINCT t FROM TableEntity t LEFT JOIN FETCH t.adjacentTables WHERE t.id IN :ids")
    List<TableEntity> findByIdInWithAdjacent(@Param("ids") List<UUID> ids);
}
