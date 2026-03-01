package com.example.CGI_Restaurant.repositories;

import com.example.CGI_Restaurant.domain.entities.QrCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Spring Data repository for {@link com.example.CGI_Restaurant.domain.entities.QrCode}.
 */
@Repository
public interface QrCodeRepository extends JpaRepository<QrCode, UUID> {
}
