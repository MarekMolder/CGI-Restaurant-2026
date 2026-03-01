package com.example.CGI_Restaurant.domain.dtos.createRequests;

import com.example.CGI_Restaurant.domain.entities.ZoneTypeEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
/** API request body for creating a zone. */
@AllArgsConstructor
@NoArgsConstructor
public class CreateZoneRequestDto {

    @NotBlank(message = "Zone name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    @NotNull(message = "Zone type is required")
    private ZoneTypeEnum type;

    @NotBlank(message = "Color is required")
    @Size(max = 50, message = "Color must not exceed 50 characters")
    private String color;

    @Valid
    private List<CreateTableEntityRequestDto> tableEntities = new ArrayList<>();
}
