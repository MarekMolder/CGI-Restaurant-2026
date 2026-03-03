package com.example.CGI_Restaurant.repositories;

import com.example.CGI_Restaurant.domain.entities.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data repository for {@link Booking}. Supports finding by customer and by id+customer for ownership checks.
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {

    /** Paginated bookings for a given customer. */
    Page<Booking> findByUserId(UUID customerId, Pageable pageable);

    /** Booking by ID only if it belongs to the given user. */
    Optional<Booking> findByIdAndUserId(UUID id, UUID userId);

    /** Booking by ID with QR codes loaded (for detail view). */
    @Query("SELECT b FROM Booking b LEFT JOIN FETCH b.qrCodes WHERE b.id = :id")
    Optional<Booking> findByIdWithQrCodes(@Param("id") UUID id);

    /** Booking by ID and user with QR codes loaded. */
    @Query("SELECT b FROM Booking b LEFT JOIN FETCH b.qrCodes WHERE b.id = :id AND b.user.id = :userId")
    Optional<Booking> findByIdAndUserIdWithQrCodes(@Param("id") UUID id, @Param("userId") UUID userId);
}
