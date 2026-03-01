package com.example.CGI_Restaurant.controllers;

/**
 * @author AI (assisted). Used my BookingControllerTest + AuthControllerTest.
 */

import com.example.CGI_Restaurant.domain.entities.MenuItem;
import com.example.CGI_Restaurant.mappers.MenuItemMapper;
import com.example.CGI_Restaurant.services.MenuItemService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MenuItemController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ControllerTestConfig.class)
class MenuItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private MenuItemMapper menuItemMapper;

    @MockitoBean
    private MenuItemService menuItemService;

    private static final UUID RESTAURANT_ID = UUID.randomUUID();
    private static final UUID MENU_ITEM_ID = UUID.randomUUID();

    @Nested
    @DisplayName("GET /api/v1/restaurants/{restaurantId}/menu")
    class ListMethod {

        @Test
        @DisplayName("returns 200 and list")
        void returns200() throws Exception {
            when(menuItemService.listByRestaurantId(RESTAURANT_ID))
                    .thenReturn(java.util.List.of(new com.example.CGI_Restaurant.domain.dtos.listResponses.MenuItemResponseDto()));

            mockMvc.perform(get("/api/v1/restaurants/{restaurantId}/menu", RESTAURANT_ID)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/restaurants/{restaurantId}/menu")
    class Create {

        @Test
        @DisplayName("returns 201")
        void returns201() throws Exception {
            var dto = new com.example.CGI_Restaurant.domain.dtos.createRequests.CreateMenuItemRequestDto();
            dto.setName("Caesar salat");
            dto.setPriceEur(new BigDecimal("8.50"));
            dto.setCategory("Eelroog");
            MenuItem created = MenuItem.builder().id(MENU_ITEM_ID).name("Caesar salat").build();
            when(menuItemMapper.fromDto(any(com.example.CGI_Restaurant.domain.dtos.createRequests.CreateMenuItemRequestDto.class))).thenReturn(new com.example.CGI_Restaurant.domain.createRequests.CreateMenuItemRequest());
            when(menuItemService.create(any())).thenReturn(created);
            when(menuItemMapper.toMenuItemResponseDto(created)).thenReturn(new com.example.CGI_Restaurant.domain.dtos.listResponses.MenuItemResponseDto());

            mockMvc.perform(post("/api/v1/restaurants/{restaurantId}/menu", RESTAURANT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated());
        }
    }

    @Nested
    @DisplayName("POST from-themealdb")
    class AddFromTheMealDB {

        @Test
        @DisplayName("returns 201")
        void returns201() throws Exception {
            var dto = new com.example.CGI_Restaurant.domain.dtos.createRequests.AddMenuItemFromTheMealDBRequestDto();
            dto.setMealId("52772");
            dto.setPriceEur(new BigDecimal("12.00"));
            MenuItem created = MenuItem.builder().id(MENU_ITEM_ID).name("Tiramisu").build();
            when(menuItemService.addFromTheMealDB(eq(RESTAURANT_ID), eq("52772"), any())).thenReturn(created);
            when(menuItemMapper.toMenuItemResponseDto(created)).thenReturn(new com.example.CGI_Restaurant.domain.dtos.listResponses.MenuItemResponseDto());

            mockMvc.perform(post("/api/v1/restaurants/{restaurantId}/menu/from-themealdb", RESTAURANT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/restaurants/{restaurantId}/menu/{menuItemId}")
    class Update {

        @Test
        @DisplayName("returns 200")
        void returns200() throws Exception {
            var dto = new com.example.CGI_Restaurant.domain.dtos.updateRequests.UpdateMenuItemRequestDto();
            dto.setId(MENU_ITEM_ID);
            dto.setName("Caesar salat uuendatud");
            MenuItem updated = MenuItem.builder().id(MENU_ITEM_ID).name("Caesar salat uuendatud").build();
            when(menuItemMapper.fromDto(any(com.example.CGI_Restaurant.domain.dtos.updateRequests.UpdateMenuItemRequestDto.class))).thenReturn(new com.example.CGI_Restaurant.domain.updateRequests.UpdateMenuItemRequest());
            when(menuItemService.update(eq(MENU_ITEM_ID), any())).thenReturn(updated);
            when(menuItemMapper.toMenuItemResponseDto(updated)).thenReturn(new com.example.CGI_Restaurant.domain.dtos.listResponses.MenuItemResponseDto());

            mockMvc.perform(put("/api/v1/restaurants/{restaurantId}/menu/{menuItemId}", RESTAURANT_ID, MENU_ITEM_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("DELETE")
    class Delete {

        @Test
        @DisplayName("returns 204")
        void returns204() throws Exception {
            mockMvc.perform(delete("/api/v1/restaurants/{restaurantId}/menu/{menuItemId}", RESTAURANT_ID, MENU_ITEM_ID))
                    .andExpect(status().isNoContent());
        }
    }
}
