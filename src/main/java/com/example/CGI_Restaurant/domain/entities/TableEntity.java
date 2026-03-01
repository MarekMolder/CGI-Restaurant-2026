package com.example.CGI_Restaurant.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * JPA entity for a physical table in a zone. Stores label, capacity, min party size, shape and position,
 * and many-to-many adjacency for combining tables. Linked to zone and seating plan.
 */
@Entity
@Table(name = "tables")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TableEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "label", nullable = false)
    private String label;

    @Column(name = "capacity", nullable = false)
    private int capacity;

    @Column(name = "min_party_size", nullable = false)
    private int minPartySize;

    @Column(name = "shape", nullable = false)
    @Enumerated(EnumType.STRING)
    private TableShapeEnum shape;

    @Column(name = "x", nullable = false)
    private double x;

    @Column(name = "y", nullable = false)
    private double y;

    @Column(name = "width", nullable = false)
    private double width;

    @Column(name = "height", nullable = false)
    private double height;

    @Column(name = "rotation_degree", nullable = false)
    private int rotationDegree;

    @Column(name = "active", nullable = false)
    private boolean active;

    @OneToMany(mappedBy = "tableEntity")
    private List<BookingTable> bookingTables = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id")
    private Zone zone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seating_plan_id")
    private SeatingPlan seatingPlan;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "table_adjacency",
            joinColumns = @JoinColumn(name = "table_id"),
            inverseJoinColumns = @JoinColumn(name = "adjacent_table_id"))
    private Set<TableEntity> adjacentTables = new HashSet<>();

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
