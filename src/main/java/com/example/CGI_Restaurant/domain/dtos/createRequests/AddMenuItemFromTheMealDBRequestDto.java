package com.example.CGI_Restaurant.domain.dtos.createRequests;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
/** API request body for adding a menu item from TheMealDB by meal id and price. */
@AllArgsConstructor
@NoArgsConstructor
public class AddMenuItemFromTheMealDBRequestDto {

    @NotBlank(message = "TheMealDB meal id is required")
    private String mealId;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0", inclusive = false, message = "Price must be positive")
    private BigDecimal priceEur;
}
