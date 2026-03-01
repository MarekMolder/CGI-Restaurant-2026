package com.example.CGI_Restaurant.domain.dtos.updateRequests;

import com.example.CGI_Restaurant.domain.dtos.createRequests.CreateTableEntityRequestDto;
import com.example.CGI_Restaurant.domain.entities.ZoneTypeEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
/** API request body for updating a zone. */
@AllArgsConstructor
@NoArgsConstructor
public class UpdateZoneRequestDto {

    @NotNull(message = "Zone ID must be provided")
    private UUID id;

    @NotBlank(message = "Zone name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    @NotNull(message = "Zone type is required")
    private ZoneTypeEnum type;

    @NotBlank(message = "Color is required")
    @Size(max = 50, message = "Color must not exceed 50 characters")
    private String color;

    @NotEmpty(message = "At least one table is required")
    @Valid
    private List<UpdateTableEntityRequestDto> tableEntities = new ArrayList<>();
}
