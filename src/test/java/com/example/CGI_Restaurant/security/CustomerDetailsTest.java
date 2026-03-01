package com.example.CGI_Restaurant.security;

import com.example.CGI_Restaurant.domain.entities.User;
import com.example.CGI_Restaurant.domain.entities.UserRoleEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CustomerDetailsTest {

    private static User createUser(UUID id, String email, UserRoleEnum role) {
        return User.builder()
                .id(id)
                .name("Test User")
                .email(email)
                .passwordHash("hashed")
                .role(role)
                .build();
    }

    @Nested
    @DisplayName("getAuthorities")
    class GetAuthorities {

        @Test
        @DisplayName("returns ROLE_CUSTOMER for customer user")
        void returnsRoleCustomer() {
            User user = createUser(UUID.randomUUID(), "customer@test.com", UserRoleEnum.CUSTOMER);
            CustomerDetails details = new CustomerDetails(user);

            var authorities = details.getAuthorities();

            assertNotNull(authorities);
            assertEquals(1, authorities.size());
            assertTrue(authorities.stream().anyMatch(a -> "ROLE_CUSTOMER".equals(a.getAuthority())));
        }

        @Test
        @DisplayName("returns ROLE_ADMIN for admin user")
        void returnsRoleAdmin() {
            User user = createUser(UUID.randomUUID(), "admin@test.com", UserRoleEnum.ADMIN);
            CustomerDetails details = new CustomerDetails(user);

            var authorities = details.getAuthorities();

            assertEquals(1, authorities.size());
            assertTrue(authorities.stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority())));
        }
    }

    @Nested
    @DisplayName("getUsername and getPassword")
    class GetUsernameAndPassword {

        @Test
        @DisplayName("getUsername returns user email")
        void getUsernameReturnsEmail() {
            User user = createUser(UUID.randomUUID(), "user@example.com", UserRoleEnum.CUSTOMER);
            CustomerDetails details = new CustomerDetails(user);
            assertEquals("user@example.com", details.getUsername());
        }

        @Test
        @DisplayName("getPassword returns user password hash")
        void getPasswordReturnsHash() {
            User user = createUser(UUID.randomUUID(), "u@t.com", UserRoleEnum.CUSTOMER);
            user.setPasswordHash("secretHash");
            CustomerDetails details = new CustomerDetails(user);
            assertEquals("secretHash", details.getPassword());
        }
    }

    @Nested
    @DisplayName("getId and getRole")
    class GetIdAndRole {

        @Test
        @DisplayName("getId returns user id")
        void getIdReturnsUserId() {
            UUID id = UUID.randomUUID();
            User user = createUser(id, "id@test.com", UserRoleEnum.CUSTOMER);
            CustomerDetails details = new CustomerDetails(user);
            assertEquals(id, details.getId());
        }

        @Test
        @DisplayName("getRole returns user role")
        void getRoleReturnsUserRole() {
            User user = createUser(UUID.randomUUID(), "r@test.com", UserRoleEnum.ADMIN);
            CustomerDetails details = new CustomerDetails(user);
            assertEquals(UserRoleEnum.ADMIN, details.getRole());
        }
    }

    @Nested
    @DisplayName("account and credentials flags")
    class AccountFlags {

        @Test
        @DisplayName("account is non-expired, non-locked, enabled; credentials non-expired")
        void allFlagsTrue() {
            User user = createUser(UUID.randomUUID(), "f@test.com", UserRoleEnum.CUSTOMER);
            CustomerDetails details = new CustomerDetails(user);

            assertTrue(details.isAccountNonExpired());
            assertTrue(details.isAccountNonLocked());
            assertTrue(details.isCredentialsNonExpired());
            assertTrue(details.isEnabled());
        }
    }
}
