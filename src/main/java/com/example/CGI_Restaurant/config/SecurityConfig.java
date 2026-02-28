package com.example.CGI_Restaurant.config;

import com.example.CGI_Restaurant.domain.entities.User;
import com.example.CGI_Restaurant.domain.entities.UserRoleEnum;
import com.example.CGI_Restaurant.repositories.UserRepository;
import com.example.CGI_Restaurant.security.CustomerDetailsService;
import com.example.CGI_Restaurant.security.JwtAuthenticationFilter;
import com.example.CGI_Restaurant.services.auth.impl.AuthenticationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(AuthenticationService authenticationService) {
        return new JwtAuthenticationFilter(authenticationService);
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        CustomerDetailsService customerDetailsService = new CustomerDetailsService(userRepository);

        String email = "admin@test.com";
        userRepository.findByEmail(email).orElseGet(() -> {
            User newAdmin = User.builder()
                    .name("Admin")
                    .email(email)
                    .role(UserRoleEnum.ADMIN)
                    .passwordHash(passwordEncoder.encode("password"))
                    .build();
            return userRepository.save(newAdmin);
        });

        return customerDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login", "/api/v1/auth/register").permitAll()
                        .requestMatchers("/api/v1/features", "/api/v1/features/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/zones", "/api/v1/seating-plans", "/api/v1/restaurants", "/api/v1/table-entities").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/zones/**", "/api/v1/seating-plans/**", "/api/v1/restaurants/**", "/api/v1/table-entities/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/zones/**", "/api/v1/seating-plans/**", "/api/v1/restaurants/**", "/api/v1/table-entities/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/restaurants/*/menu", "/api/v1/restaurants/*/menu/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/restaurants/*/menu/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/restaurants/*/menu/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/**").permitAll()
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                ).addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
