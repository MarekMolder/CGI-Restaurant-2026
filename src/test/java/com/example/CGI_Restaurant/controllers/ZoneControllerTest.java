package com.example.CGI_Restaurant.controllers;

/**
 * @author AI (assisted). Used my BookingControllerTest + AuthControllerTest.
 */

import com.example.CGI_Restaurant.domain.entities.Zone;
import com.example.CGI_Restaurant.domain.entities.ZoneTypeEnum;
import com.example.CGI_Restaurant.mappers.ZoneMapper;
import com.example.CGI_Restaurant.services.ZoneService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ZoneController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ControllerTestConfig.class)
class ZoneControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private ZoneMapper zoneMapper;

    @MockitoBean
    private ZoneService zoneService;

    private static final UUID ID = UUID.randomUUID();

    @Nested
    @DisplayName("POST /api/v1/zones")
    class Create {

        @Test
        @DisplayName("returns 201")
        void returns201() throws Exception {
            var dto = new com.example.CGI_Restaurant.domain.dtos.createRequests.CreateZoneRequestDto();
            dto.setName("Terrass");
            dto.setType(ZoneTypeEnum.TERRACE);
            dto.setColor("#2E7D32");
            Zone created = Zone.builder().id(ID).name("Terrass").type(ZoneTypeEnum.TERRACE).build();
            when(zoneMapper.fromDto(any(com.example.CGI_Restaurant.domain.dtos.createRequests.CreateZoneRequestDto.class))).thenReturn(new com.example.CGI_Restaurant.domain.createRequests.CreateZoneRequest());
            when(zoneService.create(any())).thenReturn(created);
            when(zoneMapper.toDto(created)).thenReturn(new com.example.CGI_Restaurant.domain.dtos.createResponses.CreateZoneResponseDto());

            mockMvc.perform(post("/api/v1/zones")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/zones")
    class ListMethod {

        @Test
        @DisplayName("returns 200")
        void returns200() throws Exception {
            Zone z = Zone.builder().id(ID).name("Siseala").build();
            when(zoneService.list(any())).thenReturn(new PageImpl<>(List.of(z), PageRequest.of(0, 20), 1));
            when(zoneMapper.toListZoneResponseDto(any())).thenReturn(new com.example.CGI_Restaurant.domain.dtos.listResponses.ListZoneResponseDto());

            mockMvc.perform(get("/api/v1/zones").param("page", "0").param("size", "20"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/zones/{id}")
    class GetById {

        @Test
        @DisplayName("returns 200 when found")
        void returns200WhenFound() throws Exception {
            Zone z = Zone.builder().id(ID).name("Terrass").build();
            when(zoneService.getById(ID)).thenReturn(Optional.of(z));
            when(zoneMapper.toGetZoneDetailsResponseDto(z)).thenReturn(new com.example.CGI_Restaurant.domain.dtos.getResponses.GetZoneDetailsResponseDto());

            mockMvc.perform(get("/api/v1/zones/{id}", ID))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("returns 404 when not found")
        void returns404WhenNotFound() throws Exception {
            when(zoneService.getById(any(UUID.class))).thenReturn(Optional.empty());
            mockMvc.perform(get("/api/v1/zones/{id}", UUID.randomUUID()))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/zones/{id}")
    class Update {

        @Test
        @DisplayName("returns 200")
        void returns200() throws Exception {
            var dto = new com.example.CGI_Restaurant.domain.dtos.updateRequests.UpdateZoneRequestDto();
            dto.setId(ID);
            dto.setName("Terrass uuendatud");
            dto.setType(ZoneTypeEnum.TERRACE);
            dto.setColor("#2E7D32");
            var tableDto = new com.example.CGI_Restaurant.domain.dtos.updateRequests.UpdateTableEntityRequestDto();
            tableDto.setId(UUID.randomUUID());
            tableDto.setLabel("T1");
            tableDto.setCapacity(2);
            tableDto.setMinPartySize(0);
            tableDto.setShape(com.example.CGI_Restaurant.domain.entities.TableShapeEnum.RECT);
            tableDto.setX(0.0);
            tableDto.setY(0.0);
            tableDto.setWidth(1.0);
            tableDto.setHeight(1.0);
            tableDto.setRotationDegree(0);
            dto.setTableEntities(List.of(tableDto));
            Zone updated = Zone.builder().id(ID).name("Terrass uuendatud").build();
            when(zoneMapper.fromDto(any(com.example.CGI_Restaurant.domain.dtos.updateRequests.UpdateZoneRequestDto.class))).thenReturn(new com.example.CGI_Restaurant.domain.updateRequests.UpdateZoneRequest());
            when(zoneService.update(eq(ID), any())).thenReturn(updated);
            when(zoneMapper.toUpdateZoneResponseDto(updated)).thenReturn(new com.example.CGI_Restaurant.domain.dtos.updateResponses.UpdateZoneResponseDto());

            mockMvc.perform(put("/api/v1/zones/{id}", ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/zones/{id}")
    class Delete {

        @Test
        @DisplayName("returns 204")
        void returns204() throws Exception {
            mockMvc.perform(delete("/api/v1/zones/{id}", ID))
                    .andExpect(status().isNoContent());
        }
    }
}
