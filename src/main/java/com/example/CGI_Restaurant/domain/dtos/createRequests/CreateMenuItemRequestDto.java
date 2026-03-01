package com.example.CGI_Restaurant.domain.dtos.createRequests;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
/** API request body for creating a menu item. */
@AllArgsConstructor
@NoArgsConstructor
public class CreateMenuItemRequestDto {

    @NotBlank(message = "Name is required")
    @Size(max = 255)
    private String name;

    @Size(max = 2000)
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0", inclusive = false, message = "Price must be positive")
    private BigDecimal priceEur;

    @Size(max = 100)
    private String category;

    @Size(max = 512)
    private String imageUrl;
}
