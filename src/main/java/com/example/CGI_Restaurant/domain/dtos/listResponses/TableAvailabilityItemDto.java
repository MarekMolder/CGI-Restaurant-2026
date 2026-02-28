package com.example.CGI_Restaurant.domain.dtos.listResponses;

import com.example.CGI_Restaurant.domain.entities.TableShapeEnum;
import com.example.CGI_Restaurant.domain.entities.ZoneTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * One table in the "available tables" search result for broneerimine.
 * Includes whether the table is free at the requested time (isAvailable)
 * and optional recommendation score for laua soovitamine (higher = better fit).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TableAvailabilityItemDto {
    private UUID id;
    private String label;
    private int capacity;
    private int minPartySize;
    private TableShapeEnum shape;
    private double x;
    private double y;
    private double width;
    private double height;
    private int rotationDegree;
    private UUID zoneId;
    private String zoneName;
    private ZoneTypeEnum zoneType;
    /** True if the table is free at the requested startAt–endAt (not already booked). */
    private boolean available;
    /**
     * Optional score for recommendation: better fit for party size and preferences = higher.
     * Only set for available tables when recommendation logic is used.
     */
    private Integer recommendationScore;
}
