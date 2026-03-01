package com.example.CGI_Restaurant.controllers;

/**
 * @author AI (assisted). Used my BookingControllerTest + AuthControllerTest.
 */

import com.example.CGI_Restaurant.domain.entities.Feature;
import com.example.CGI_Restaurant.domain.entities.FeatureCodeEnum;
import com.example.CGI_Restaurant.mappers.FeatureMapper;
import com.example.CGI_Restaurant.services.FeatureService;
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

@WebMvcTest(FeatureController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ControllerTestConfig.class)
class FeatureControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private FeatureMapper featureMapper;

    @MockitoBean
    private FeatureService featureService;

    private static final UUID ID = UUID.randomUUID();

    @Nested
    @DisplayName("POST /api/v1/features")
    class Create {

        @Test
        @DisplayName("returns 201")
        void returns201() throws Exception {
            var dto = new com.example.CGI_Restaurant.domain.dtos.createRequests.CreateFeatureRequestDto();
            dto.setCode(FeatureCodeEnum.WINDOW);
            dto.setName("Aknaäärne");
            Feature created = Feature.builder().id(ID).code(FeatureCodeEnum.WINDOW).name("Aknaäärne").build();
            when(featureMapper.fromDto(any(com.example.CGI_Restaurant.domain.dtos.createRequests.CreateFeatureRequestDto.class))).thenReturn(new com.example.CGI_Restaurant.domain.createRequests.CreateFeatureRequest());
            when(featureService.create(any())).thenReturn(created);
            when(featureMapper.toDto(created)).thenReturn(new com.example.CGI_Restaurant.domain.dtos.createResponses.CreateFeatureResponseDto());

            mockMvc.perform(post("/api/v1/features")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/features")
    class ListMethod {

        @Test
        @DisplayName("returns 200")
        void returns200() throws Exception {
            Feature f = Feature.builder().id(ID).name("Window").build();
            when(featureService.list(any())).thenReturn(new PageImpl<>(List.of(f), PageRequest.of(0, 20), 1));
            when(featureMapper.toListFeatureResponseDto(any())).thenReturn(new com.example.CGI_Restaurant.domain.dtos.listResponses.ListFeatureResponseDto());

            mockMvc.perform(get("/api/v1/features").param("page", "0").param("size", "20"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/features/{id}")
    class GetById {

        @Test
        @DisplayName("returns 200 when found")
        void returns200WhenFound() throws Exception {
            Feature f = Feature.builder().id(ID).name("Window").build();
            when(featureService.getById(ID)).thenReturn(Optional.of(f));
            when(featureMapper.toGetFeatureDetailsResponseDto(f)).thenReturn(new com.example.CGI_Restaurant.domain.dtos.getResponses.GetFeatureDetailsResponseDto());

            mockMvc.perform(get("/api/v1/features/{id}", ID))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("returns 404 when not found")
        void returns404WhenNotFound() throws Exception {
            when(featureService.getById(any(UUID.class))).thenReturn(Optional.empty());
            mockMvc.perform(get("/api/v1/features/{id}", UUID.randomUUID()))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/features/{id}")
    class Update {

        @Test
        @DisplayName("returns 200")
        void returns200() throws Exception {
            var dto = new com.example.CGI_Restaurant.domain.dtos.updateRequests.UpdateFeatureRequestDto();
            dto.setId(ID);
            dto.setCode(com.example.CGI_Restaurant.domain.entities.FeatureCodeEnum.WINDOW);
            dto.setName("Aknaäärne uuendatud");
            Feature updated = Feature.builder().id(ID).name("Aknaäärne uuendatud").build();
            when(featureMapper.fromDto(any(com.example.CGI_Restaurant.domain.dtos.updateRequests.UpdateFeatureRequestDto.class))).thenReturn(new com.example.CGI_Restaurant.domain.updateRequests.UpdateFeatureRequest());
            when(featureService.update(eq(ID), any())).thenReturn(updated);
            when(featureMapper.toUpdateFeatureResponseDto(updated)).thenReturn(new com.example.CGI_Restaurant.domain.dtos.updateResponses.UpdateFeatureResponseDto());

            mockMvc.perform(put("/api/v1/features/{id}", ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/features/{id}")
    class Delete {

        @Test
        @DisplayName("returns 204")
        void returns204() throws Exception {
            mockMvc.perform(delete("/api/v1/features/{id}", ID))
                    .andExpect(status().isNoContent());
        }
    }
}
