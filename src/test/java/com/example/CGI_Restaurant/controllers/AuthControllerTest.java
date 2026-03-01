package com.example.CGI_Restaurant.controllers;

import com.example.CGI_Restaurant.domain.dtos.LoginRequest;
import com.example.CGI_Restaurant.domain.dtos.RegisterRequest;
import com.example.CGI_Restaurant.services.auth.impl.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private AuthenticationService authenticationService;

    @Nested
    @DisplayName("POST /api/v1/auth/login")
    class Login {

        @Test
        @DisplayName("returns 200 and token when credentials valid")
        void returnsTokenWhenValid() throws Exception {
            UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                    .username("mari@example.ee")
                    .password("encoded")
                    .roles("CUSTOMER")
                    .build();
            when(authenticationService.authenticate("mari@example.ee", "password")).thenReturn(userDetails);
            when(authenticationService.generateToken(userDetails)).thenReturn("jwt-token-123");

            String body = objectMapper.writeValueAsString(new LoginRequest("mari@example.ee", "password"));

            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value("jwt-token-123"))
                    .andExpect(jsonPath("$.expiresIn").value(86400));
        }

        @Test
        @DisplayName("returns 400 when body invalid")
        void returns400WhenInvalidBody() throws Exception {
            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("returns 401 when credentials are invalid")
        void returns401WhenBadCredentials() throws Exception {
            when(authenticationService.authenticate(anyString(), anyString()))
                    .thenThrow(new BadCredentialsException("Bad credentials"));

            String body = objectMapper.writeValueAsString(new LoginRequest("wrong@example.com", "wrongpass"));

            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value("Incorrect username or password"));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/auth/register")
    class Register {

        @Test
        @DisplayName("returns 201 and token when registration successful")
        void returnsTokenWhenSuccess() throws Exception {
            doNothing().when(authenticationService).register(anyString(), anyString(), anyString());
            UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                    .username("new@example.ee")
                    .password("encoded")
                    .roles("CUSTOMER")
                    .build();
            when(authenticationService.authenticate("new@example.ee", "password123")).thenReturn(userDetails);
            when(authenticationService.generateToken(userDetails)).thenReturn("new-jwt");

            String body = objectMapper.writeValueAsString(RegisterRequest.builder()
                    .name("Mari Kask")
                    .email("new@example.ee")
                    .password("password123")
                    .build());

            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.token").value("new-jwt"));
        }

        @Test
        @DisplayName("returns 400 when validation fails")
        void returns400WhenValidationFails() throws Exception {
            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"\",\"email\":\"bad\",\"password\":\"short\"}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("returns 409 when email already exists")
        void returns409WhenEmailAlreadyExists() throws Exception {
            doThrow(new IllegalStateException("User with email duplicate@example.com already exists"))
                    .when(authenticationService).register(eq("duplicate@example.com"), anyString(), anyString());

            String body = objectMapper.writeValueAsString(RegisterRequest.builder()
                    .name("Duplicate User")
                    .email("duplicate@example.com")
                    .password("validpass123")
                    .build());

            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("User with email duplicate@example.com already exists"));
        }
    }
}
