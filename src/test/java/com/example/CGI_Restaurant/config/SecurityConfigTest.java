package com.example.CGI_Restaurant.config;

import com.example.CGI_Restaurant.domain.dtos.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for security configuration: public vs protected endpoints,
 * and role-based access (ADMIN).
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(com.example.CGI_Restaurant.TestMailConfig.class)
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Nested
    @DisplayName("Public endpoints (no authentication)")
    class PublicEndpoints {

        @Test
        @DisplayName("POST /api/v1/auth/login is permitted without token")
        void loginPermittedWithoutToken() throws Exception {
            String body = objectMapper.writeValueAsString(
                    new LoginRequest("admin@test.com", "password"));

            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("POST /api/v1/auth/register is permitted without token")
        void registerPermittedWithoutToken() throws Exception {
            String body = objectMapper.writeValueAsString(
                    com.example.CGI_Restaurant.domain.dtos.RegisterRequest.builder()
                            .name("New User")
                            .email("newuser" + System.currentTimeMillis() + "@test.com")
                            .password("password123")
                            .build());

            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("GET /api/v1/restaurants is permitted without token")
        void getRestaurantsPermittedWithoutToken() throws Exception {
            mockMvc.perform(get("/api/v1/restaurants"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Admin-only endpoints")
    class AdminOnlyEndpoints {

        @Test
        @DisplayName("GET /api/v1/features returns 403 without token (features require ADMIN)")
        void getFeaturesForbiddenWithoutToken() throws Exception {
            mockMvc.perform(get("/api/v1/features"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("POST /api/v1/features returns 403 without token")
        void postFeaturesForbiddenWithoutToken() throws Exception {
            String body = "{\"code\":\"WINDOW\",\"name\":\"Window\",\"bookingPreferences\":[]}";

            mockMvc.perform(post("/api/v1/features")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("POST /api/v1/restaurants returns 403 without token")
        void postRestaurantsForbiddenWithoutToken() throws Exception {
            String body = "{\"name\":\"R\",\"timezone\":\"UTC\",\"email\":\"r@r.com\",\"phone\":\"1\",\"address\":\"A\",\"seatingPlans\":[]}";

            mockMvc.perform(post("/api/v1/restaurants")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isForbidden());
        }
    }
}
