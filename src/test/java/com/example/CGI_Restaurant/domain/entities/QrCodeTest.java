package com.example.CGI_Restaurant.domain.entities;

/**
 * @author AI (assisted). Used my BookingTest + UserTest.
 */

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author AI (assisted). Used my BookingTest.
 */

import static org.junit.jupiter.api.Assertions.*;

class QrCodeTest {

    @Nested
    @DisplayName("Builder")
    class BuilderTests {

        @Test
        @DisplayName("Builder creates QR code with all attributes")
        void builderCreatesQrCodeWithAllFields() {
            UUID id = UUID.randomUUID();
            LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
            LocalDateTime updatedAt = LocalDateTime.now();

            QrCode qrCode = QrCode.builder()
                    .id(id)
                    .status(QrCodeStatusEnum.ACTIVE)
                    .value("123456789")
                    .booking(null)
                    .createdAt(createdAt)
                    .updatedAt(updatedAt)
                    .build();

            assertNotNull(qrCode);
            assertEquals(id, qrCode.getId());
            assertEquals(QrCodeStatusEnum.ACTIVE, qrCode.getStatus());
            assertEquals("123456789", qrCode.getValue());
            assertEquals(createdAt, qrCode.getCreatedAt());
            assertEquals(updatedAt, qrCode.getUpdatedAt());
        }

        @Test
        @DisplayName("Builder with EXPIRED status")
        void builderWithExpiredStatus() {
            QrCode qrCode = QrCode.builder()
                    .id(UUID.randomUUID())
                    .status(QrCodeStatusEnum.EXPIRED)
                    .value("token-xyz")
                    .build();

            assertEquals(QrCodeStatusEnum.EXPIRED, qrCode.getStatus());
        }
    }

    @Nested
    @DisplayName("No-args constructor and setters")
    class ConstructorAndSetters {

        @Test
        @DisplayName("No-args constructor creates empty QR code")
        void noArgsConstructorCreatesEmptyQrCode() {
            QrCode qrCode = new QrCode();

            assertNotNull(qrCode);
            assertNull(qrCode.getId());
            assertNull(qrCode.getStatus());
            assertNull(qrCode.getValue());
            assertNull(qrCode.getBooking());
        }

        @Test
        @DisplayName("Setters and getters work")
        void settersAndGettersWork() {
            QrCode qrCode = new QrCode();
            UUID id = UUID.randomUUID();
            LocalDateTime now = LocalDateTime.now();

            qrCode.setId(id);
            qrCode.setStatus(QrCodeStatusEnum.ACTIVE);
            qrCode.setValue("new-value");
            qrCode.setCreatedAt(now);
            qrCode.setUpdatedAt(now);

            assertEquals(id, qrCode.getId());
            assertEquals(QrCodeStatusEnum.ACTIVE, qrCode.getStatus());
            assertEquals("new-value", qrCode.getValue());
            assertEquals(now, qrCode.getCreatedAt());
        }
    }
}
