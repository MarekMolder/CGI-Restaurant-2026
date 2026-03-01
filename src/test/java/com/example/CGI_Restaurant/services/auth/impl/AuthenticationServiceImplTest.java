package com.example.CGI_Restaurant.services.auth.impl;

import com.example.CGI_Restaurant.domain.entities.User;
import com.example.CGI_Restaurant.domain.entities.UserRoleEnum;
import com.example.CGI_Restaurant.repositories.UserRepository;
import com.example.CGI_Restaurant.security.CustomerDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    private static final String JWT_SECRET = "test-jwt-secret-for-unit-tests-only-min-256-bits-required-for-HS256";

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authenticationService, "secretKey", JWT_SECRET);
    }

    @Nested
    @DisplayName("authenticate")
    class Authenticate {

        @Test
        @DisplayName("delegates to authentication manager and returns user details")
        void returnsUserDetailsWhenCredentialsValid() {
            UserDetails userDetails = new CustomerDetails(createUser("user@test.com"));
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(null);
            when(userDetailsService.loadUserByUsername("user@test.com")).thenReturn(userDetails);

            UserDetails result = authenticationService.authenticate("user@test.com", "password");

            assertNotNull(result);
            assertEquals("user@test.com", result.getUsername());
            verify(authenticationManager).authenticate(
                    new UsernamePasswordAuthenticationToken("user@test.com", "password"));
            verify(userDetailsService).loadUserByUsername("user@test.com");
        }

        @Test
        @DisplayName("throws when authentication manager rejects credentials")
        void throwsWhenBadCredentials() {
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new BadCredentialsException("Bad credentials"));

            assertThrows(BadCredentialsException.class,
                    () -> authenticationService.authenticate("wrong@test.com", "wrong"));
            verify(userDetailsService, never()).loadUserByUsername(anyString());
        }
    }

    @Nested
    @DisplayName("register")
    class Register {

        @Test
        @DisplayName("saves new user with encoded password and CUSTOMER role")
        void savesNewUser() {
            when(userRepository.findByEmail("new@test.com")).thenReturn(Optional.empty());
            when(passwordEncoder.encode("plainpassword")).thenReturn("encoded");
            when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

            authenticationService.register("new@test.com", "plainpassword", "New User");

            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(captor.capture());
            User saved = captor.getValue();
            assertEquals("New User", saved.getName());
            assertEquals("new@test.com", saved.getEmail());
            assertEquals("encoded", saved.getPasswordHash());
            assertEquals(UserRoleEnum.CUSTOMER, saved.getRole());
        }

        @Test
        @DisplayName("throws IllegalStateException when email already exists")
        void throwsWhenEmailExists() {
            when(userRepository.findByEmail("existing@test.com"))
                    .thenReturn(Optional.of(createUser("existing@test.com")));

            IllegalStateException ex = assertThrows(IllegalStateException.class,
                    () -> authenticationService.register("existing@test.com", "pass", "Existing"));
            assertTrue(ex.getMessage().contains("already exists"));
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("generateToken")
    class GenerateToken {

        @Test
        @DisplayName("returns non-empty JWT for user details")
        void returnsJwtToken() {
            UserDetails userDetails = new CustomerDetails(createUser("token@test.com"));

            String token = authenticationService.generateToken(userDetails);

            assertNotNull(token);
            assertFalse(token.isBlank());
            assertTrue(token.split("\\.").length == 3); // JWT has 3 parts
        }

        @Test
        @DisplayName("token contains subject as username")
        void tokenSubjectIsUsername() {
            UserDetails userDetails = new CustomerDetails(createUser("subject@test.com"));
            when(userDetailsService.loadUserByUsername("subject@test.com")).thenReturn(userDetails);
            String token = authenticationService.generateToken(userDetails);
            UserDetails validated = authenticationService.validateToken(token);
            assertEquals("subject@test.com", validated.getUsername());
        }
    }

    @Nested
    @DisplayName("validateToken")
    class ValidateToken {

        @Test
        @DisplayName("returns user details when token is valid")
        void returnsUserDetailsWhenTokenValid() {
            UserDetails userDetails = new CustomerDetails(createUser("validate@test.com"));
            when(userDetailsService.loadUserByUsername("validate@test.com")).thenReturn(userDetails);
            String token = authenticationService.generateToken(userDetails);

            UserDetails result = authenticationService.validateToken(token);

            assertNotNull(result);
            assertEquals("validate@test.com", result.getUsername());
        }
    }

    private static User createUser(String email) {
        return User.builder()
                .id(UUID.randomUUID())
                .name("Test User")
                .email(email)
                .passwordHash("encoded")
                .role(UserRoleEnum.CUSTOMER)
                .build();
    }
}
