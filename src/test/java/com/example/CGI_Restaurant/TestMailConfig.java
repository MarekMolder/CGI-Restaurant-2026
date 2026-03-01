package com.example.CGI_Restaurant;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * Provides a JavaMailSender bean for tests so that the full application context can load
 * without a real mail server (e.g. for CgiRestaurantApplicationTests).
 */
@Configuration
public class TestMailConfig {

	@Bean
	public JavaMailSender javaMailSender() {
		return new JavaMailSenderImpl();
	}
}
