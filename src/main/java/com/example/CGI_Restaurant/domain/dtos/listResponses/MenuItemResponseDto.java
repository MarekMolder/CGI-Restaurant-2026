package com.example.CGI_Restaurant.domain.dtos.listResponses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
/** API response for a menu item (list or single). */
@AllArgsConstructor
@NoArgsConstructor
public class MenuItemResponseDto {
    private UUID id;
    private String name;
    private String description;
    private BigDecimal priceEur;
    private String category;
    private String imageUrl;
}
