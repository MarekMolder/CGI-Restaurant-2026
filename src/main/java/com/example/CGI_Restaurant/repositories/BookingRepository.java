package com.example.CGI_Restaurant.repositories;

import com.example.CGI_Restaurant.domain.entities.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {
    Page<Booking> findByUserId(UUID customerId, Pageable pageable);
    Optional<Booking> findByIdAndUserId(UUID id, UUID userId);
}
