package com.example.CGI_Restaurant.controllers;

/**
 * @author AI (assisted). Used my BookingControllerTest + AuthControllerTest.
 */

import com.example.CGI_Restaurant.services.TheMealDBService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TheMealDBController.class)
@AutoConfigureMockMvc(addFilters = false)
class TheMealDBControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TheMealDBService theMealDBService;

    @Nested
    @DisplayName("GET /api/v1/themealdb/categories")
    class GetCategories {

        @Test
        @DisplayName("returns 200 and list of categories")
        void returnsCategories() throws Exception {
            var cat = new TheMealDBService.TheMealDBCategoryDto("1", "Dessert", "thumb", "desc");
            when(theMealDBService.getCategories()).thenReturn(List.of(cat));

            mockMvc.perform(get("/api/v1/themealdb/categories"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].strCategory").value("Dessert"));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/themealdb/meals")
    class GetMealsByCategory {

        @Test
        @DisplayName("returns 200 and list of meals")
        void returnsMeals() throws Exception {
            var meal = new TheMealDBService.TheMealDBMealSummaryDto("1", "Tiramisu", "thumb");
            when(theMealDBService.getMealsByCategory("Dessert")).thenReturn(List.of(meal));

            mockMvc.perform(get("/api/v1/themealdb/meals").param("category", "Dessert"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].idMeal").value("1"));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/themealdb/meals/{id}")
    class GetMealById {

        @Test
        @DisplayName("returns 200 when meal found")
        void returnsMealWhenFound() throws Exception {
            var detail = new TheMealDBService.TheMealDBMealDetailDto("1", "Tiramisu", "Dessert", "instructions", "thumb");
            when(theMealDBService.getMealById("52772")).thenReturn(detail);

            mockMvc.perform(get("/api/v1/themealdb/meals/52772"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.idMeal").value("1"));
        }

        @Test
        @DisplayName("returns 404 when meal not found")
        void returns404WhenNotFound() throws Exception {
            when(theMealDBService.getMealById("99999")).thenReturn(null);

            mockMvc.perform(get("/api/v1/themealdb/meals/99999"))
                    .andExpect(status().isNotFound());
        }
    }
}
