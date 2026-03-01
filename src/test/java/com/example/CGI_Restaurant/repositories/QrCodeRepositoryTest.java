package com.example.CGI_Restaurant.repositories;

/**
 * @author AI (assisted). Used my BookingRepositoryTest + UserRepositoryTest.
 */

import com.example.CGI_Restaurant.config.JpaTestConfig;
import com.example.CGI_Restaurant.domain.entities.QrCode;
import com.example.CGI_Restaurant.domain.entities.QrCodeStatusEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(JpaTestConfig.class)
@ActiveProfiles("test")
class QrCodeRepositoryTest {

    @Autowired
    private QrCodeRepository repository;

    private static QrCode newQrCode(UUID id) {
        var now = java.time.LocalDateTime.now();
        return QrCode.builder()
                .id(id != null ? id : UUID.randomUUID())
                .status(QrCodeStatusEnum.ACTIVE)
                .value("123456789")
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    @Nested
    @DisplayName("Save and find")
    class SaveAndFind {

        @Test
        @DisplayName("Saves and finds by id")
        void saveAndFindById() {
            UUID id = UUID.randomUUID();
            QrCode qr = repository.save(newQrCode(id));
            assertEquals(id, qr.getId());

            Optional<QrCode> found = repository.findById(id);
            assertTrue(found.isPresent());
            assertEquals(QrCodeStatusEnum.ACTIVE, found.get().getStatus());
        }

        @Test
        @DisplayName("FindById returns empty when not found")
        void findByIdReturnsEmptyWhenNotFound() {
            assertTrue(repository.findById(UUID.randomUUID()).isEmpty());
        }
    }
}
