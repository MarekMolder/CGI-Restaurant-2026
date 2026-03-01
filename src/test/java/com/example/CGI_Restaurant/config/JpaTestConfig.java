package com.example.CGI_Restaurant.config;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Config for @DataJpaTest slice so that entities and repositories are discovered
 * without loading the full application context.
 */
@Configuration
@EntityScan("com.example.CGI_Restaurant.domain.entities")
@EnableJpaRepositories("com.example.CGI_Restaurant.repositories")
@EnableJpaAuditing
public class JpaTestConfig {
}
