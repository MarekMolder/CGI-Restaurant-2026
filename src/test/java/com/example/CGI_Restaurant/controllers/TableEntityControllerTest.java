package com.example.CGI_Restaurant.controllers;

/**
 * @author AI (assisted). Used my BookingControllerTest + AuthControllerTest.
 */

import com.example.CGI_Restaurant.domain.dtos.listResponses.TableAvailabilityItemDto;
import com.example.CGI_Restaurant.domain.entities.TableEntity;
import com.example.CGI_Restaurant.domain.entities.TableShapeEnum;
import com.example.CGI_Restaurant.mappers.TableEntityMapper;
import com.example.CGI_Restaurant.services.TableEntityService;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TableEntityController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ControllerTestConfig.class)
class TableEntityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private TableEntityMapper tableEntityMapper;

    @MockitoBean
    private TableEntityService tableEntityService;

    private static final UUID ID = UUID.randomUUID();

    @Nested
    @DisplayName("POST /api/v1/table-entities")
    class Create {

        @Test
        @DisplayName("returns 201")
        void returns201() throws Exception {
            var dto = new com.example.CGI_Restaurant.domain.dtos.createRequests.CreateTableEntityRequestDto();
            dto.setLabel("Laud 1");
            dto.setCapacity(4);
            dto.setMinPartySize(2);
            dto.setShape(TableShapeEnum.RECT);
            dto.setX(10.0);
            dto.setY(20.0);
            dto.setWidth(80.0);
            dto.setHeight(120.0);
            dto.setRotationDegree(0);
            dto.setActive(true);
            TableEntity created = TableEntity.builder().id(ID).label("Laud 1").build();
            when(tableEntityMapper.fromDto(any(com.example.CGI_Restaurant.domain.dtos.createRequests.CreateTableEntityRequestDto.class))).thenReturn(new com.example.CGI_Restaurant.domain.createRequests.CreateTableEntityRequest());
            when(tableEntityService.create(any())).thenReturn(created);
            when(tableEntityMapper.toDto(created)).thenReturn(new com.example.CGI_Restaurant.domain.dtos.createResponses.CreateTableEntityResponseDto());

            mockMvc.perform(post("/api/v1/table-entities")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/table-entities")
    class ListMethod {

        @Test
        @DisplayName("returns 200 without q")
        void returns200WithoutQ() throws Exception {
            TableEntity t = TableEntity.builder().id(ID).label("Laud 1").build();
            when(tableEntityService.list(any())).thenReturn(new PageImpl<>(List.of(t), PageRequest.of(0, 20), 1));
            when(tableEntityMapper.toListTableEntityResponseDto(any())).thenReturn(new com.example.CGI_Restaurant.domain.dtos.listResponses.ListTableEntityResponseDto());

            mockMvc.perform(get("/api/v1/table-entities").param("page", "0").param("size", "20"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("returns 200 with q uses search")
        void returns200WithQ() throws Exception {
            TableEntity t = TableEntity.builder().id(ID).label("Laud").build();
            when(tableEntityService.searchAvailableTables(eq("laud"), any())).thenReturn(new PageImpl<>(List.of(t), PageRequest.of(0, 20), 1));
            when(tableEntityMapper.toListTableEntityResponseDto(any())).thenReturn(new com.example.CGI_Restaurant.domain.dtos.listResponses.ListTableEntityResponseDto());

            mockMvc.perform(get("/api/v1/table-entities").param("q", "laud").param("page", "0").param("size", "20"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/table-entities/{id}")
    class GetById {

        @Test
        @DisplayName("returns 200 when found")
        void returns200WhenFound() throws Exception {
            TableEntity t = TableEntity.builder().id(ID).label("Laud 1").build();
            when(tableEntityService.getById(ID)).thenReturn(Optional.of(t));
            when(tableEntityMapper.toGetTableEntityDetailsResponseDto(t)).thenReturn(new com.example.CGI_Restaurant.domain.dtos.getResponses.GetTableEntityDetailsResponseDto());

            mockMvc.perform(get("/api/v1/table-entities/{id}", ID))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("returns 404 when not found")
        void returns404WhenNotFound() throws Exception {
            when(tableEntityService.getById(any(UUID.class))).thenReturn(Optional.empty());
            mockMvc.perform(get("/api/v1/table-entities/{id}", UUID.randomUUID()))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/table-entities/{id}")
    class Update {

        @Test
        @DisplayName("returns 200")
        void returns200() throws Exception {
            var dto = new com.example.CGI_Restaurant.domain.dtos.updateRequests.UpdateTableEntityRequestDto();
            dto.setId(ID);
            dto.setLabel("Laud 1 uuendatud");
            dto.setCapacity(2);
            dto.setMinPartySize(0);
            dto.setShape(com.example.CGI_Restaurant.domain.entities.TableShapeEnum.RECT);
            dto.setX(0.0);
            dto.setY(0.0);
            dto.setWidth(1.0);
            dto.setHeight(1.0);
            dto.setRotationDegree(0);
            TableEntity updated = TableEntity.builder().id(ID).label("Laud 1 uuendatud").build();
            when(tableEntityMapper.fromDto(any(com.example.CGI_Restaurant.domain.dtos.updateRequests.UpdateTableEntityRequestDto.class))).thenReturn(new com.example.CGI_Restaurant.domain.updateRequests.UpdateTableEntityRequest());
            when(tableEntityService.update(eq(ID), any())).thenReturn(updated);
            when(tableEntityMapper.toUpdateTableEntityResponseDto(updated)).thenReturn(new com.example.CGI_Restaurant.domain.dtos.updateResponses.UpdateTableEntityResponseDto());

            mockMvc.perform(put("/api/v1/table-entities/{id}", ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/table-entities/{id}")
    class Delete {

        @Test
        @DisplayName("returns 204")
        void returns204() throws Exception {
            mockMvc.perform(delete("/api/v1/table-entities/{id}", ID))
                    .andExpect(status().isNoContent());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/table-entities/available")
    class GetAvailable {

        @Test
        @DisplayName("returns 400 when both seatingPlanId and zoneId null")
        void returns400WhenBothNull() throws Exception {
            mockMvc.perform(get("/api/v1/table-entities/available")
                            .param("partySize", "2")
                            .param("startAt", LocalDateTime.now().toString())
                            .param("endAt", LocalDateTime.now().plusHours(2).toString()))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("returns 200 when zoneId provided")
        void returns200WhenZoneId() throws Exception {
            when(tableEntityService.findTablesWithAvailability(isNull(), eq(ID), eq(2), any(), any(), any()))
                    .thenReturn(java.util.List.of(new TableAvailabilityItemDto()));

            mockMvc.perform(get("/api/v1/table-entities/available")
                            .param("zoneId", ID.toString())
                            .param("partySize", "2")
                            .param("startAt", LocalDateTime.now().toString())
                            .param("endAt", LocalDateTime.now().plusHours(2).toString()))
                    .andExpect(status().isOk());
        }
    }
}
