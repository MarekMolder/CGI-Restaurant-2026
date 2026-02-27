package com.example.CGI_Restaurant.domain.dtos.getResponses;

import com.example.CGI_Restaurant.domain.dtos.listResponses.ListBookingTableResponseDto;
import com.example.CGI_Restaurant.domain.entities.TableShapeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetTableEntityDetailsResponseDto {
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
    private boolean active;
    private List<GetBookingTableDetailsResponseDto> bookingTables = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
