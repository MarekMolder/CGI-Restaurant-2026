package com.example.CGI_Restaurant.domain.updateRequests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/** Request to update a menu item (id and product fields). */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMenuItemRequest {
    private UUID id;
    private String name;
    private String description;
    private BigDecimal priceEur;
    private String category;
    private String imageUrl;
}
