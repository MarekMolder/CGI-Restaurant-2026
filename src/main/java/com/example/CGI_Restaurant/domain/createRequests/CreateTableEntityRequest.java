package com.example.CGI_Restaurant.domain.createRequests;

import com.example.CGI_Restaurant.domain.entities.TableShapeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateTableEntityRequest {

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
    private List<CreateBookingTableRequest> bookingTables = new ArrayList<>();
}
