package com.example.CGI_Restaurant.domain.dtos.updateRequests;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
/** API request body for updating a menu item. */
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMenuItemRequestDto {

    @NotNull
    private UUID id;

    @Size(max = 255)
    private String name;

    @Size(max = 2000)
    private String description;

    @DecimalMin(value = "0", inclusive = false, message = "Price must be positive")
    private BigDecimal priceEur;

    @Size(max = 100)
    private String category;

    @Size(max = 512)
    private String imageUrl;
}
