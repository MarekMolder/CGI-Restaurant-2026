package com.example.CGI_Restaurant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class CgiRestaurantApplication {

	public static void main(String[] args) {
		SpringApplication.run(CgiRestaurantApplication.class, args);
	}

}
