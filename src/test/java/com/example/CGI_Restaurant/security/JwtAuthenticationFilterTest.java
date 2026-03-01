package com.example.CGI_Restaurant.security;

import com.example.CGI_Restaurant.domain.entities.User;
import com.example.CGI_Restaurant.domain.entities.UserRoleEnum;
import com.example.CGI_Restaurant.services.auth.impl.AuthenticationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private jakarta.servlet.http.HttpServletRequest request;

    @Mock
    private jakarta.servlet.http.HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter filter;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("when no Authorization header")
    class NoAuthorizationHeader {

        @Test
        @DisplayName("does not validate token and invokes chain")
        void continuesWithoutAuth() throws ServletException, IOException {
            when(request.getHeader("Authorization")).thenReturn(null);
            filter = new JwtAuthenticationFilter(authenticationService);

            filter.doFilter(request, response, filterChain);

            verify(authenticationService, never()).validateToken(anyString());
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("does not validate when header has no Bearer prefix")
        void ignoresNonBearerHeader() throws ServletException, IOException {
            when(request.getHeader("Authorization")).thenReturn("Basic abc123");
            filter = new JwtAuthenticationFilter(authenticationService);

            filter.doFilter(request, response, filterChain);

            verify(authenticationService, never()).validateToken(anyString());
            verify(filterChain).doFilter(request, response);
        }
    }

    @Nested
    @DisplayName("when valid Bearer token")
    class ValidBearerToken {

        @Test
        @DisplayName("validates token and sets authentication in context")
        void setsSecurityContext() throws ServletException, IOException {
            when(request.getHeader("Authorization")).thenReturn("Bearer valid-jwt-token");
            User user = User.builder()
                    .id(UUID.randomUUID())
                    .name("User")
                    .email("user@test.com")
                    .passwordHash("hash")
                    .role(UserRoleEnum.CUSTOMER)
                    .build();
            CustomerDetails userDetails = new CustomerDetails(user);
            when(authenticationService.validateToken("valid-jwt-token")).thenReturn(userDetails);
            filter = new JwtAuthenticationFilter(authenticationService);

            filter.doFilter(request, response, filterChain);

            verify(authenticationService).validateToken("valid-jwt-token");
            assertNotNull(SecurityContextHolder.getContext().getAuthentication());
            assertEquals("user@test.com", SecurityContextHolder.getContext().getAuthentication().getName());
            verify(filterChain).doFilter(request, response);
        }

        @Test
        @DisplayName("sets userId request attribute when user details are CustomerDetails")
        void setsUserIdAttribute() throws ServletException, IOException {
            UUID userId = UUID.randomUUID();
            when(request.getHeader("Authorization")).thenReturn("Bearer token");
            User user = User.builder()
                    .id(userId)
                    .name("User")
                    .email("u@t.com")
                    .passwordHash("h")
                    .role(UserRoleEnum.CUSTOMER)
                    .build();
            when(authenticationService.validateToken("token")).thenReturn(new CustomerDetails(user));
            filter = new JwtAuthenticationFilter(authenticationService);

            filter.doFilter(request, response, filterChain);

            ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<Object> valueCaptor = ArgumentCaptor.forClass(Object.class);
            verify(request, atLeastOnce()).setAttribute(keyCaptor.capture(), valueCaptor.capture());
            int idx = keyCaptor.getAllValues().indexOf("userId");
            assertTrue(idx >= 0);
            assertEquals(userId, valueCaptor.getAllValues().get(idx));
        }
    }

    @Nested
    @DisplayName("when token is invalid")
    class InvalidToken {

        @Test
        @DisplayName("continues chain without setting auth and does not propagate exception")
        void continuesWithoutAuthOnInvalidToken() throws ServletException, IOException {
            when(request.getHeader("Authorization")).thenReturn("Bearer invalid-token");
            when(authenticationService.validateToken("invalid-token")).thenThrow(new RuntimeException("Invalid JWT"));
            filter = new JwtAuthenticationFilter(authenticationService);

            filter.doFilter(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
            assertNull(SecurityContextHolder.getContext().getAuthentication());
        }
    }
}
