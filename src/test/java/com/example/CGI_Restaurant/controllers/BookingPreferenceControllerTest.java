package com.example.CGI_Restaurant.controllers;

/**
 * @author AI (assisted). Used my BookingControllerTest + AuthControllerTest.
 */

import com.example.CGI_Restaurant.domain.entities.BookingPreference;
import com.example.CGI_Restaurant.domain.entities.PreferencePriorityEnum;
import com.example.CGI_Restaurant.mappers.BookingPreferenceMapper;
import com.example.CGI_Restaurant.services.BookingPreferenceService;
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

@WebMvcTest(BookingPreferenceController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ControllerTestConfig.class)
class BookingPreferenceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private BookingPreferenceMapper bookingPreferenceMapper;

    @MockitoBean
    private BookingPreferenceService bookingPreferenceService;

    private static final UUID ID = UUID.randomUUID();

    @Nested
    @DisplayName("POST /api/v1/booking-preferences")
    class Create {

        @Test
        @DisplayName("returns 201")
        void returns201() throws Exception {
            var dto = new com.example.CGI_Restaurant.domain.dtos.createRequests.CreateBookingPreferenceRequestDto();
            dto.setBookingId(UUID.randomUUID());
            dto.setFeatureId(UUID.randomUUID());
            dto.setPriority(PreferencePriorityEnum.HIGH);
            BookingPreference created = BookingPreference.builder().id(ID).priority(PreferencePriorityEnum.HIGH).build();
            when(bookingPreferenceMapper.fromDto(any(com.example.CGI_Restaurant.domain.dtos.createRequests.CreateBookingPreferenceRequestDto.class))).thenReturn(new com.example.CGI_Restaurant.domain.createRequests.CreateBookingPreferenceRequest());
            when(bookingPreferenceService.create(any())).thenReturn(created);
            when(bookingPreferenceMapper.toDto(created)).thenReturn(new com.example.CGI_Restaurant.domain.dtos.createResponses.CreateBookingPreferenceResponseDto());

            mockMvc.perform(post("/api/v1/booking-preferences")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/booking-preferences")
    class ListMethod {

        @Test
        @DisplayName("returns 200")
        void returns200() throws Exception {
            BookingPreference bp = BookingPreference.builder().id(ID).build();
            when(bookingPreferenceService.list(any())).thenReturn(new PageImpl<>(List.of(bp), PageRequest.of(0, 20), 1));
            when(bookingPreferenceMapper.toListBookingPreferenceResponseDto(any())).thenReturn(new com.example.CGI_Restaurant.domain.dtos.listResponses.ListBookingPreferenceResponseDto());

            mockMvc.perform(get("/api/v1/booking-preferences"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/booking-preferences/{id}")
    class GetById {

        @Test
        @DisplayName("returns 200 when found")
        void returns200WhenFound() throws Exception {
            BookingPreference bp = BookingPreference.builder().id(ID).build();
            when(bookingPreferenceService.getById(ID)).thenReturn(Optional.of(bp));
            when(bookingPreferenceMapper.toGetBookingPreferenceDetailsResponseDto(bp)).thenReturn(new com.example.CGI_Restaurant.domain.dtos.getResponses.GetBookingPreferenceDetailsResponseDto());

            mockMvc.perform(get("/api/v1/booking-preferences/{id}", ID))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("returns 404 when not found")
        void returns404WhenNotFound() throws Exception {
            when(bookingPreferenceService.getById(any(UUID.class))).thenReturn(Optional.empty());
            mockMvc.perform(get("/api/v1/booking-preferences/{id}", UUID.randomUUID()))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/booking-preferences/{id}")
    class Update {

        @Test
        @DisplayName("returns 200")
        void returns200() throws Exception {
            var dto = new com.example.CGI_Restaurant.domain.dtos.updateRequests.UpdateBookingPreferenceRequestDto();
            dto.setId(ID);
            dto.setPriority(PreferencePriorityEnum.LOW);
            BookingPreference updated = BookingPreference.builder().id(ID).priority(PreferencePriorityEnum.LOW).build();
            when(bookingPreferenceMapper.fromDto(any(com.example.CGI_Restaurant.domain.dtos.updateRequests.UpdateBookingPreferenceRequestDto.class))).thenReturn(new com.example.CGI_Restaurant.domain.updateRequests.UpdateBookingPreferenceRequest());
            when(bookingPreferenceService.update(eq(ID), any())).thenReturn(updated);
            when(bookingPreferenceMapper.toUpdateBookingPreferenceResponseDto(updated)).thenReturn(new com.example.CGI_Restaurant.domain.dtos.updateResponses.UpdateBookingPreferenceResponseDto());

            mockMvc.perform(put("/api/v1/booking-preferences/{id}", ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/booking-preferences/{id}")
    class Delete {

        @Test
        @DisplayName("returns 204")
        void returns204() throws Exception {
            mockMvc.perform(delete("/api/v1/booking-preferences/{id}", ID))
                    .andExpect(status().isNoContent());
        }
    }
}
