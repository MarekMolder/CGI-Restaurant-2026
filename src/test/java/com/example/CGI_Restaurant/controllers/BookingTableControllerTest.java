package com.example.CGI_Restaurant.controllers;

/**
 * @author AI (assisted). Used my BookingControllerTest + AuthControllerTest.
 */

import com.example.CGI_Restaurant.domain.entities.BookingTable;
import com.example.CGI_Restaurant.mappers.BookingTableMapper;
import com.example.CGI_Restaurant.services.BookingTableService;
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

@WebMvcTest(BookingTableController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ControllerTestConfig.class)
class BookingTableControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private BookingTableMapper bookingTableMapper;

    @MockitoBean
    private BookingTableService bookingTableService;

    private static final UUID ID = UUID.randomUUID();

    @Nested
    @DisplayName("POST /api/v1/booking-tables")
    class Create {

        @Test
        @DisplayName("returns 201")
        void returns201() throws Exception {
            var dto = new com.example.CGI_Restaurant.domain.dtos.createRequests.CreateBookingTableRequestDto();
            dto.setBookingId(UUID.randomUUID());
            dto.setTableEntityId(UUID.randomUUID());
            BookingTable created = BookingTable.builder().id(ID).build();
            when(bookingTableMapper.fromDto(any(com.example.CGI_Restaurant.domain.dtos.createRequests.CreateBookingTableRequestDto.class))).thenReturn(new com.example.CGI_Restaurant.domain.createRequests.CreateBookingTableRequest());
            when(bookingTableService.create(any())).thenReturn(created);
            when(bookingTableMapper.toDto(created)).thenReturn(new com.example.CGI_Restaurant.domain.dtos.createResponses.CreateBookingTableResponseDto());

            mockMvc.perform(post("/api/v1/booking-tables")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/booking-tables")
    class ListMethod {

        @Test
        @DisplayName("returns 200")
        void returns200() throws Exception {
            BookingTable bt = BookingTable.builder().id(ID).build();
            when(bookingTableService.list(any())).thenReturn(new PageImpl<>(List.of(bt), PageRequest.of(0, 20), 1));
            when(bookingTableMapper.toListBookingTableResponseDto(any())).thenReturn(new com.example.CGI_Restaurant.domain.dtos.listResponses.ListBookingTableResponseDto());

            mockMvc.perform(get("/api/v1/booking-tables"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/booking-tables/{id}")
    class GetById {

        @Test
        @DisplayName("returns 200 when found")
        void returns200WhenFound() throws Exception {
            BookingTable bt = BookingTable.builder().id(ID).build();
            when(bookingTableService.getById(ID)).thenReturn(Optional.of(bt));
            when(bookingTableMapper.toGetBookingTableDetailsResponseDto(bt)).thenReturn(new com.example.CGI_Restaurant.domain.dtos.getResponses.GetBookingTableDetailsResponseDto());

            mockMvc.perform(get("/api/v1/booking-tables/{id}", ID))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("returns 404 when not found")
        void returns404WhenNotFound() throws Exception {
            when(bookingTableService.getById(any(UUID.class))).thenReturn(Optional.empty());
            mockMvc.perform(get("/api/v1/booking-tables/{id}", UUID.randomUUID()))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/booking-tables/{id}")
    class Update {

        @Test
        @DisplayName("returns 200")
        void returns200() throws Exception {
            var dto = new com.example.CGI_Restaurant.domain.dtos.updateRequests.UpdateBookingTableRequestDto();
            dto.setId(ID);
            BookingTable updated = BookingTable.builder().id(ID).build();
            when(bookingTableMapper.fromDto(any(com.example.CGI_Restaurant.domain.dtos.updateRequests.UpdateBookingTableRequestDto.class))).thenReturn(new com.example.CGI_Restaurant.domain.updateRequests.UpdateBookingTableRequest());
            when(bookingTableService.update(eq(ID), any())).thenReturn(updated);
            when(bookingTableMapper.toUpdateBookingTableResponseDto(updated)).thenReturn(new com.example.CGI_Restaurant.domain.dtos.updateResponses.UpdateBookingTableResponseDto());

            mockMvc.perform(put("/api/v1/booking-tables/{id}", ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/booking-tables/{id}")
    class Delete {

        @Test
        @DisplayName("returns 204")
        void returns204() throws Exception {
            mockMvc.perform(delete("/api/v1/booking-tables/{id}", ID))
                    .andExpect(status().isNoContent());
        }
    }
}
