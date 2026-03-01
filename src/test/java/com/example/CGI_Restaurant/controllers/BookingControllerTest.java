package com.example.CGI_Restaurant.controllers;

import com.example.CGI_Restaurant.domain.entities.Booking;
import com.example.CGI_Restaurant.domain.entities.BookingStatusEnum;
import com.example.CGI_Restaurant.mappers.BookingMapper;
import com.example.CGI_Restaurant.security.CustomerDetails;
import com.example.CGI_Restaurant.services.BookingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ControllerTestConfig.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @MockitoBean
    private BookingMapper bookingMapper;

    @MockitoBean
    private BookingService bookingService;

    private static final UUID BOOKING_ID = UUID.randomUUID();

    private CustomerDetails adminPrincipal() {
        return ControllerTestSupport.adminPrincipal();
    }

    private CustomerDetails customerPrincipal() {
        return ControllerTestSupport.customerPrincipal(UUID.randomUUID());
    }

    @Nested
    @DisplayName("POST /api/v1/bookings")
    class Create {

        @Test
        @DisplayName("returns 201 when authenticated")
        void returns201() throws Exception {
            var dto = new com.example.CGI_Restaurant.domain.dtos.createRequests.CreateBookingRequestDto();
            dto.setGuestName("Mari Kask");
            dto.setGuestEmail("mari@example.ee");
            dto.setStartAt(LocalDateTime.now().plusDays(1));
            dto.setEndAt(LocalDateTime.now().plusDays(1).plusHours(2));
            dto.setPartySize(2);
            dto.setStatus(BookingStatusEnum.PENDING);
            Booking created = Booking.builder().id(BOOKING_ID).guestName("Mari Kask").qrCodes(new java.util.ArrayList<>()).build();
            when(bookingMapper.fromDto(any(com.example.CGI_Restaurant.domain.dtos.createRequests.CreateBookingRequestDto.class))).thenReturn(new com.example.CGI_Restaurant.domain.createRequests.CreateBookingRequest());
            when(bookingService.createBooking(any())).thenReturn(created);
            when(bookingMapper.toDto(created)).thenReturn(new com.example.CGI_Restaurant.domain.dtos.createResponses.CreateBookingResponseDto());

            mockMvc.perform(post("/api/v1/bookings")
                            .with(ControllerTestSupport.withPrincipal(customerPrincipal()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/bookings")
    class ListMethod {

        @Test
        @DisplayName("returns 200 for admin")
        void returns200Admin() throws Exception {
            Booking b = Booking.builder().id(BOOKING_ID).build();
            when(bookingService.listBookingsForAdmin(any())).thenReturn(new PageImpl<>(List.of(b), PageRequest.of(0, 20), 1));
            when(bookingMapper.toListBookingResponseDto(any())).thenReturn(new com.example.CGI_Restaurant.domain.dtos.listResponses.ListBookingResponseDto());

            mockMvc.perform(get("/api/v1/bookings").param("page", "0").param("size", "20").with(ControllerTestSupport.withPrincipal(adminPrincipal())))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("returns 200 for customer")
        void returns200Customer() throws Exception {
            UUID customerId = UUID.randomUUID();
            Booking b = Booking.builder().id(BOOKING_ID).build();
            when(bookingService.listBookingForCustomer(eq(customerId), any())).thenReturn(new PageImpl<>(List.of(b), PageRequest.of(0, 20), 1));
            when(bookingMapper.toListBookingResponseDto(any())).thenReturn(new com.example.CGI_Restaurant.domain.dtos.listResponses.ListBookingResponseDto());

            mockMvc.perform(get("/api/v1/bookings").param("page", "0").param("size", "20").with(ControllerTestSupport.withPrincipal(ControllerTestSupport.customerPrincipal(customerId))))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("GET /api/v1/bookings/{bookingId}")
    class GetById {

        @Test
        @DisplayName("returns 200 when found for admin")
        void returns200Admin() throws Exception {
            Booking b = Booking.builder().id(BOOKING_ID).build();
            when(bookingService.getBooking(BOOKING_ID)).thenReturn(Optional.of(b));
            when(bookingMapper.toGetBookingDetailsResponseDto(b)).thenReturn(new com.example.CGI_Restaurant.domain.dtos.getResponses.GetBookingDetailsResponseDto());

            mockMvc.perform(get("/api/v1/bookings/{bookingId}", BOOKING_ID).with(ControllerTestSupport.withPrincipal(adminPrincipal())))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("returns 404 when not found")
        void returns404WhenNotFound() throws Exception {
            when(bookingService.getBooking(any(UUID.class))).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/v1/bookings/{bookingId}", UUID.randomUUID()).with(ControllerTestSupport.withPrincipal(adminPrincipal())))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/bookings/{bookingId}")
    class Update {

        @Test
        @DisplayName("returns 200 for admin")
        void returns200Admin() throws Exception {
            var dto = new com.example.CGI_Restaurant.domain.dtos.updateRequests.UpdateBookingRequestDto();
            dto.setId(BOOKING_ID);
            dto.setGuestName("Gregor");
            dto.setGuestEmail("gregor@example.ee");
            dto.setStartAt(LocalDateTime.now().plusDays(1));
            dto.setEndAt(LocalDateTime.now().plusDays(1).plusHours(2));
            dto.setPartySize(4);
            dto.setStatus(BookingStatusEnum.CONFIRMED);
            dto.setQrToken("qr-token-for-test");
            dto.setBookingPreferences(List.of());
            Booking updated = Booking.builder().id(BOOKING_ID).guestName("Gregor").build();
            when(bookingMapper.fromDto(any(com.example.CGI_Restaurant.domain.dtos.updateRequests.UpdateBookingRequestDto.class))).thenReturn(new com.example.CGI_Restaurant.domain.updateRequests.UpdateBookingRequest());
            when(bookingService.updateBooking(eq(BOOKING_ID), any())).thenReturn(updated);
            when(bookingMapper.toUpdateBookingResponseDto(updated)).thenReturn(new com.example.CGI_Restaurant.domain.dtos.updateResponses.UpdateBookingResponseDto());

            mockMvc.perform(put("/api/v1/bookings/{bookingId}", BOOKING_ID)
                            .with(ControllerTestSupport.withPrincipal(adminPrincipal()))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/bookings/{bookingId}")
    class Delete {

        @Test
        @DisplayName("returns 204 for admin")
        void returns204Admin() throws Exception {
            mockMvc.perform(delete("/api/v1/bookings/{bookingId}", BOOKING_ID).with(ControllerTestSupport.withPrincipal(adminPrincipal())))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("returns 204 for customer")
        void returns204Customer() throws Exception {
            mockMvc.perform(delete("/api/v1/bookings/{bookingId}", BOOKING_ID).with(ControllerTestSupport.withPrincipal(customerPrincipal())))
                    .andExpect(status().isNoContent());
        }
    }
}
