package com.example.CGI_Restaurant.controllers;

/**
 * @author AI (assisted). Used my BookingControllerTest + AuthControllerTest.
 */

import com.example.CGI_Restaurant.domain.entities.Restaurant;
import com.example.CGI_Restaurant.mappers.RestaurantMapper;
import com.example.CGI_Restaurant.services.RestaurantService;
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

@WebMvcTest(RestaurantController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ControllerTestConfig.class)
class RestaurantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private RestaurantMapper restaurantMapper;

    @MockitoBean
    private RestaurantService restaurantService;

    private static final UUID ID = UUID.randomUUID();

    @Nested
    @DisplayName("POST /api/v1/restaurants")
    class Create {

        @Test
        @DisplayName("returns 201 and created body")
        void returns201() throws Exception {
            var dto = new com.example.CGI_Restaurant.domain.dtos.createRequests.CreateRestaurantRequestDto();
            dto.setName("Resto Aed");
            dto.setTimezone("Europe/Tallinn");
            dto.setEmail("info@restoaed.ee");
            dto.setPhone("+372 6123456");
            dto.setAddress("Pärnu mnt 1, Tallinn");
            Restaurant created = Restaurant.builder().id(ID).name("Resto Aed").build();
            when(restaurantMapper.fromDto(any(com.example.CGI_Restaurant.domain.dtos.createRequests.CreateRestaurantRequestDto.class))).thenReturn(new com.example.CGI_Restaurant.domain.createRequests.CreateRestaurantRequest());
            when(restaurantService.create(any())).thenReturn(created);
            when(restaurantMapper.toDto(created)).thenReturn(new com.example.CGI_Restaurant.domain.dtos.createResponses.CreateRestaurantResponseDto());

            mockMvc.perform(post("/api/v1/restaurants")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/restaurants")
    class ListMethod {

        @Test
        @DisplayName("returns 200 and page")
        void returns200() throws Exception {
            Restaurant r = Restaurant.builder().id(ID).name("Resto").build();
            when(restaurantService.list(any())).thenReturn(new PageImpl<>(List.of(r), PageRequest.of(0, 20), 1));
            when(restaurantMapper.toListRestaurantResponseDto(any())).thenReturn(new com.example.CGI_Restaurant.domain.dtos.listResponses.ListRestaurantResponseDto());

            mockMvc.perform(get("/api/v1/restaurants").param("page", "0").param("size", "20"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/restaurants/{id}")
    class GetById {

        @Test
        @DisplayName("returns 200 when found")
        void returns200WhenFound() throws Exception {
            Restaurant r = Restaurant.builder().id(ID).name("Resto").build();
            when(restaurantService.getById(ID)).thenReturn(Optional.of(r));
            when(restaurantMapper.toGetRestaurantDetailsResponseDto(r)).thenReturn(new com.example.CGI_Restaurant.domain.dtos.getResponses.GetRestaurantDetailsResponseDto());

            mockMvc.perform(get("/api/v1/restaurants/{id}", ID))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("returns 404 when not found")
        void returns404WhenNotFound() throws Exception {
            when(restaurantService.getById(any(UUID.class))).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/v1/restaurants/{id}", UUID.randomUUID()))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/restaurants/{id}")
    class Update {

        @Test
        @DisplayName("returns 200 and updated body")
        void returns200() throws Exception {
            var dto = new com.example.CGI_Restaurant.domain.dtos.updateRequests.UpdateRestaurantRequestDto();
            dto.setId(ID);
            dto.setName("Resto Aed Uuendatud");
            dto.setTimezone("Europe/Tallinn");
            dto.setEmail("info@restoaed.ee");
            dto.setPhone("+372 6123456");
            dto.setAddress("Pärnu mnt 1");
            Restaurant updated = Restaurant.builder().id(ID).name("Resto Aed Uuendatud").build();
            when(restaurantMapper.fromDto(any(com.example.CGI_Restaurant.domain.dtos.updateRequests.UpdateRestaurantRequestDto.class))).thenReturn(new com.example.CGI_Restaurant.domain.updateRequests.UpdateRestaurantRequest());
            when(restaurantService.update(eq(ID), any())).thenReturn(updated);
            when(restaurantMapper.toUpdateRestaurantResponseDto(updated)).thenReturn(new com.example.CGI_Restaurant.domain.dtos.updateResponses.UpdateRestaurantResponseDto());

            mockMvc.perform(put("/api/v1/restaurants/{id}", ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/restaurants/{id}")
    class Delete {

        @Test
        @DisplayName("returns 204")
        void returns204() throws Exception {
            mockMvc.perform(delete("/api/v1/restaurants/{id}", ID))
                    .andExpect(status().isNoContent());
        }
    }
}
