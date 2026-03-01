package com.example.CGI_Restaurant.repositories;

import com.example.CGI_Restaurant.domain.entities.BookingPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Spring Data repository for {@link BookingPreference}.
 */
@Repository
public interface BookingPreferenceRepository extends JpaRepository<BookingPreference, UUID> {
}
