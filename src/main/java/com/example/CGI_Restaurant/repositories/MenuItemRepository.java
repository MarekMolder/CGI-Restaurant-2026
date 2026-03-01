package com.example.CGI_Restaurant.repositories;

import com.example.CGI_Restaurant.domain.entities.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data repository for {@link com.example.CGI_Restaurant.domain.entities.MenuItem}.
 */
@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, UUID> {

    /** Returns menu items for a restaurant ordered by category and name. */
    List<MenuItem> findByRestaurantIdOrderByCategoryAscNameAsc(UUID restaurantId);
}
