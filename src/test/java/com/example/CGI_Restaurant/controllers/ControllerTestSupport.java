package com.example.CGI_Restaurant.controllers;

/**
 * @author AI (assisted). Used my BookingControllerTest + AuthControllerTest.
 */

import com.example.CGI_Restaurant.domain.entities.User;
import com.example.CGI_Restaurant.domain.entities.UserRoleEnum;
import com.example.CGI_Restaurant.security.CustomerDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.UUID;

/**
 * Shared support for controller tests: principal (CustomerDetails) for BookingController etc.
 */
public final class ControllerTestSupport {

    private ControllerTestSupport() {}

    public static CustomerDetails adminPrincipal() {
        User admin = User.builder()
                .id(UUID.randomUUID())
                .name("Admin")
                .email("admin@test.com")
                .passwordHash("hash")
                .role(UserRoleEnum.ADMIN)
                .build();
        return new CustomerDetails(admin);
    }

    public static CustomerDetails customerPrincipal(UUID userId) {
        User customer = User.builder()
                .id(userId != null ? userId : UUID.randomUUID())
                .name("Mari Kask")
                .email("mari@example.ee")
                .passwordHash("hash")
                .role(UserRoleEnum.CUSTOMER)
                .build();
        return new CustomerDetails(customer);
    }

    public static RequestPostProcessor withPrincipal(CustomerDetails principal) {
        return request -> {
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
            return request;
        };
    }
}
