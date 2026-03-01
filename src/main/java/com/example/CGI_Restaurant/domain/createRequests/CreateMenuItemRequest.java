package com.example.CGI_Restaurant.domain.createRequests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/** Request to create a menu item for a restaurant. */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateMenuItemRequest {
    private String name;
    private String description;
    private BigDecimal priceEur;
    private String category;
    private String imageUrl;
    private UUID restaurantId;
}
