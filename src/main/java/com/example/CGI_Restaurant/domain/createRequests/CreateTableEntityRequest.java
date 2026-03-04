package com.example.CGI_Restaurant.domain.createRequests;

import com.example.CGI_Restaurant.domain.entities.TableShapeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Request to create a table entity: label, capacity, shape, position, zone, features and optional adjacent table IDs. */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTableEntityRequest {

    private UUID zoneId;
    private String label;
    private int capacity;
    private int minPartySize;
    private TableShapeEnum shape;
    private double x;
    private double y;
    private double width;
    private double height;
    private int rotationDegree;
    private boolean active;
    private List<UUID> featureIds = new ArrayList<>();
    private List<UUID> adjacentTableIds = new ArrayList<>();
}
