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

/**
 * Spring Data repository for {@link TableEntity}. Supports search by label and loading by zone/seating plan with adjacent tables for availability.
 */
@Repository
public interface TableEntityRepository extends JpaRepository<TableEntity, UUID> {

    /** Search active tables by label (case-insensitive). */
    @Query("SELECT t FROM TableEntity t WHERE t.active = true AND LOWER(t.label) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<TableEntity> searchTableEntities(@Param("searchTerm") String searchTerm, Pageable pageable);

    Page<TableEntity> findByZoneIdAndActiveTrue(UUID zoneId, Pageable pageable);

    Page<TableEntity> findBySeatingPlanIdAndActiveTrue(UUID seatingPlanId, Pageable pageable);

    /** Load active tables in a zone with adjacent tables fetched (for availability and combined tables). */
    @Query("SELECT DISTINCT t FROM TableEntity t LEFT JOIN FETCH t.adjacentTables WHERE t.zone.id = :zoneId AND t.active = true")
    List<TableEntity> findByZoneIdAndActiveTrueWithAdjacent(@Param("zoneId") UUID zoneId);

    /** Load active tables in a seating plan with adjacent tables fetched. */
    @Query("SELECT DISTINCT t FROM TableEntity t LEFT JOIN FETCH t.adjacentTables WHERE t.seatingPlan.id = :seatingPlanId AND t.active = true")
    List<TableEntity> findBySeatingPlanIdAndActiveTrueWithAdjacent(@Param("seatingPlanId") UUID seatingPlanId);

    /** Load tables by IDs with adjacent tables fetched (for adjacency validation). */
    @Query("SELECT DISTINCT t FROM TableEntity t LEFT JOIN FETCH t.adjacentTables WHERE t.id IN :ids")
    List<TableEntity> findByIdInWithAdjacent(@Param("ids") List<UUID> ids);
}
