package com.example.CGI_Restaurant.controllers;

/**
 * @author AI (assisted). Used my BookingControllerTest + AuthControllerTest.
 */

import com.example.CGI_Restaurant.domain.entities.SeatingPlan;
import com.example.CGI_Restaurant.domain.entities.SeatingPlanTypeEnum;
import com.example.CGI_Restaurant.mappers.SeatingPlanMapper;
import com.example.CGI_Restaurant.services.SeatingPlanService;
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

@WebMvcTest(SeatingPlanController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ControllerTestConfig.class)
class SeatingPlanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private SeatingPlanMapper seatingPlanMapper;

    @MockitoBean
    private SeatingPlanService seatingPlanService;

    private static final UUID ID = UUID.randomUUID();

    @Nested
    @DisplayName("POST /api/v1/seating-plans")
    class Create {

        @Test
        @DisplayName("returns 201")
        void returns201() throws Exception {
            var dto = new com.example.CGI_Restaurant.domain.dtos.createRequests.CreateSeatingPlanRequestDto();
            dto.setName("Põhiplaan");
            dto.setType(SeatingPlanTypeEnum.FLOOR_1);
            dto.setWidth(800.0);
            dto.setHeight(600.0);
            dto.setVersion(0);
            SeatingPlan created = SeatingPlan.builder().id(ID).name("Põhiplaan").build();
            when(seatingPlanMapper.fromDto(any(com.example.CGI_Restaurant.domain.dtos.createRequests.CreateSeatingPlanRequestDto.class))).thenReturn(new com.example.CGI_Restaurant.domain.createRequests.CreateSeatingPlanRequest());
            when(seatingPlanService.create(any())).thenReturn(created);
            when(seatingPlanMapper.toDto(created)).thenReturn(new com.example.CGI_Restaurant.domain.dtos.createResponses.CreateSeatingPlanResponseDto());

            mockMvc.perform(post("/api/v1/seating-plans")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/seating-plans")
    class ListMethod {

        @Test
        @DisplayName("returns 200")
        void returns200() throws Exception {
            SeatingPlan sp = SeatingPlan.builder().id(ID).name("Põhiplaan").build();
            when(seatingPlanService.list(any())).thenReturn(new PageImpl<>(List.of(sp), PageRequest.of(0, 20), 1));
            when(seatingPlanMapper.toListSeatingPlanResponseDto(any())).thenReturn(new com.example.CGI_Restaurant.domain.dtos.listResponses.ListSeatingPlanResponseDto());

            mockMvc.perform(get("/api/v1/seating-plans").param("page", "0").param("size", "20"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/seating-plans/{id}")
    class GetById {

        @Test
        @DisplayName("returns 200 when found")
        void returns200WhenFound() throws Exception {
            SeatingPlan sp = SeatingPlan.builder().id(ID).name("Põhiplaan").build();
            when(seatingPlanService.getById(ID)).thenReturn(Optional.of(sp));
            when(seatingPlanMapper.toGetSeatingPlanDetailsResponseDto(sp)).thenReturn(new com.example.CGI_Restaurant.domain.dtos.getResponses.GetSeatingPlanDetailsResponseDto());

            mockMvc.perform(get("/api/v1/seating-plans/{id}", ID))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("returns 404 when not found")
        void returns404WhenNotFound() throws Exception {
            when(seatingPlanService.getById(any(UUID.class))).thenReturn(Optional.empty());
            mockMvc.perform(get("/api/v1/seating-plans/{id}", UUID.randomUUID()))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/seating-plans/{id}")
    class Update {

        @Test
        @DisplayName("returns 200")
        void returns200() throws Exception {
            var dto = new com.example.CGI_Restaurant.domain.dtos.updateRequests.UpdateSeatingPlanRequestDto();
            dto.setId(ID);
            dto.setName("Põhiplaan uuendatud");
            dto.setType(com.example.CGI_Restaurant.domain.entities.SeatingPlanTypeEnum.FLOOR_1);
            dto.setWidth(10.0);
            dto.setHeight(10.0);
            dto.setVersion(0);
            SeatingPlan updated = SeatingPlan.builder().id(ID).name("Põhiplaan uuendatud").build();
            when(seatingPlanMapper.fromDto(any(com.example.CGI_Restaurant.domain.dtos.updateRequests.UpdateSeatingPlanRequestDto.class))).thenReturn(new com.example.CGI_Restaurant.domain.updateRequests.UpdateSeatingPlanRequest());
            when(seatingPlanService.update(eq(ID), any())).thenReturn(updated);
            when(seatingPlanMapper.toUpdateSeatingPlanResponseDto(updated)).thenReturn(new com.example.CGI_Restaurant.domain.dtos.updateResponses.UpdateSeatingPlanResponseDto());

            mockMvc.perform(put("/api/v1/seating-plans/{id}", ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/seating-plans/{id}")
    class Delete {

        @Test
        @DisplayName("returns 204")
        void returns204() throws Exception {
            mockMvc.perform(delete("/api/v1/seating-plans/{id}", ID))
                    .andExpect(status().isNoContent());
        }
    }
}
