package com.example.CGI_Restaurant.repositories;

import com.example.CGI_Restaurant.domain.entities.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Spring Data repository for {@link com.example.CGI_Restaurant.domain.entities.Restaurant}.
 */
@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {
}
