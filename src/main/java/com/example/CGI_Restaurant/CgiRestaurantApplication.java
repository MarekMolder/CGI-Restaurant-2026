package com.example.CGI_Restaurant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

/**
 * Entry point for the CGI Restaurant application. Spring Boot app with JPA auditing enabled;
 * provides REST API for bookings, tables, restaurants, menu, auth and admin resources.
 */
@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class CgiRestaurantApplication {

	public static void main(String[] args) {
		SpringApplication.run(CgiRestaurantApplication.class, args);
	}

}
