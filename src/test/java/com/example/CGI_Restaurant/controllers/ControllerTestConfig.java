package com.example.CGI_Restaurant.controllers;

/**
 * @author AI (assisted).
 */

import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

/**
 * Ensures PageableHandlerMethodArgumentResolver is available in @WebMvcTest slices
 * so GET list endpoints with Pageable resolve page/size/sort reliably (avoids flaky 400).
 */
@Configuration
@EnableSpringDataWebSupport
public class ControllerTestConfig {
}
