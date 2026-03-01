package com.example.CGI_Restaurant.security;

import com.example.CGI_Restaurant.domain.entities.User;
import com.example.CGI_Restaurant.domain.entities.UserRoleEnum;
import com.example.CGI_Restaurant.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomerDetailsService customerDetailsService;

    @Nested
    @DisplayName("loadUserByUsername")
    class LoadUserByUsername {

        @Test
        @DisplayName("returns CustomerDetails when user exists")
        void returnsCustomerDetailsWhenUserExists() {
            User user = User.builder()
                    .id(UUID.randomUUID())
                    .name("John")
                    .email("john@test.com")
                    .passwordHash("hash")
                    .role(UserRoleEnum.CUSTOMER)
                    .build();
            when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.of(user));

            UserDetails result = customerDetailsService.loadUserByUsername("john@test.com");

            assertNotNull(result);
            assertTrue(result instanceof CustomerDetails);
            assertEquals("john@test.com", result.getUsername());
            assertEquals("hash", result.getPassword());
            assertEquals(1, result.getAuthorities().size());
            assertTrue(result.getAuthorities().stream()
                    .anyMatch(a -> "ROLE_CUSTOMER".equals(a.getAuthority())));
        }

        @Test
        @DisplayName("throws UsernameNotFoundException when user not found")
        void throwsWhenUserNotFound() {
            when(userRepository.findByEmail("missing@test.com")).thenReturn(Optional.empty());

            UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class,
                    () -> customerDetailsService.loadUserByUsername("missing@test.com"));

            assertTrue(ex.getMessage().contains("missing@test.com"));
            assertTrue(ex.getMessage().contains("not found"));
        }
    }
}
