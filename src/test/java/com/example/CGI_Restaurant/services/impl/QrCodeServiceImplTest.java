package com.example.CGI_Restaurant.services.impl;

/**
 * @author AI (assisted). Used my BookingSeviceImplTest.
 */

import com.example.CGI_Restaurant.domain.entities.Booking;
import com.example.CGI_Restaurant.domain.entities.QrCode;
import com.example.CGI_Restaurant.domain.entities.QrCodeStatusEnum;
import com.example.CGI_Restaurant.exceptions.QrCodeGenerationException;
import com.example.CGI_Restaurant.repositories.QrCodeRepository;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QrCodeServiceImplTest {

    @Mock
    private QrCodeRepository qrCodeRepository;

    @Mock
    private Clock clock;

    @InjectMocks
    private QrCodeServiceImpl service;

    @Nested
    @DisplayName("generateQrCode")
    class GenerateQrCode {

        @Test
        @DisplayName("saves QrCode with booking and ACTIVE status when writer succeeds")
        void savesQrCodeWithBookingAndActiveStatus() throws Exception {
            QRCodeWriter realWriter = new QRCodeWriter();
            QrCodeServiceImpl serviceWithRealWriter = new QrCodeServiceImpl(realWriter, qrCodeRepository, Clock.systemDefaultZone());

            Booking booking = Booking.builder().id(UUID.randomUUID()).guestName("Mari Kask").guestEmail("mari@example.ee").build();
            when(qrCodeRepository.saveAndFlush(any(QrCode.class))).thenAnswer(inv -> inv.getArgument(0));

            QrCode result = serviceWithRealWriter.generateQrCode(booking);

            assertNotNull(result);
            assertEquals(QrCodeStatusEnum.ACTIVE, result.getStatus());
            assertEquals(booking, result.getBooking());
            assertNotNull(result.getValue());
            assertNotNull(result.getId());

            ArgumentCaptor<QrCode> captor = ArgumentCaptor.forClass(QrCode.class);
            verify(qrCodeRepository).saveAndFlush(captor.capture());
            assertEquals(booking, captor.getValue().getBooking());
        }

        @Test
        @DisplayName("throws QrCodeGenerationException when writer throws")
        void throwsWhenWriterFails() throws Exception {
            QRCodeWriter failingWriter = mock(QRCodeWriter.class);
            when(failingWriter.encode(anyString(), any(), anyInt(), anyInt())).thenThrow(new WriterException("mock"));
            QrCodeServiceImpl serviceWithFailingWriter = new QrCodeServiceImpl(failingWriter, qrCodeRepository, Clock.systemDefaultZone());

            Booking booking = Booking.builder().id(UUID.randomUUID()).build();

            QrCodeGenerationException ex = assertThrows(QrCodeGenerationException.class,
                    () -> serviceWithFailingWriter.generateQrCode(booking));
            assertTrue(ex.getMessage().contains("Failed to generate QR Code"));
            verify(qrCodeRepository, never()).saveAndFlush(any());
        }
    }

    @Nested
    @DisplayName("markExpiredIfBookingEnded")
    class MarkExpiredIfBookingEnded {

        @Test
        @DisplayName("sets status to EXPIRED and saves when booking end is in the past")
        void marksExpiredWhenBookingEnded() {
            LocalDateTime pastEnd = LocalDateTime.of(2025, 2, 1, 14, 0);
            LocalDateTime now = LocalDateTime.of(2025, 2, 15, 12, 0);
            Instant instant = now.atZone(ZoneId.systemDefault()).toInstant();
            when(clock.getZone()).thenReturn(ZoneId.systemDefault());
            when(clock.instant()).thenReturn(instant);

            Booking booking = Booking.builder().id(UUID.randomUUID()).endAt(pastEnd).build();
            QrCode qrCode = QrCode.builder().id(UUID.randomUUID()).status(QrCodeStatusEnum.ACTIVE).booking(booking).value("x").build();
            when(qrCodeRepository.saveAndFlush(any(QrCode.class))).thenAnswer(inv -> inv.getArgument(0));

            QrCode result = service.markExpiredIfBookingEnded(qrCode);

            assertEquals(QrCodeStatusEnum.EXPIRED, result.getStatus());
            verify(qrCodeRepository).saveAndFlush(qrCode);
        }

        @Test
        @DisplayName("does not change status when booking end is in the future")
        void leavesActiveWhenBookingNotEnded() {
            LocalDateTime futureEnd = LocalDateTime.of(2025, 12, 1, 14, 0);
            LocalDateTime now = LocalDateTime.of(2025, 2, 15, 12, 0);
            when(clock.getZone()).thenReturn(ZoneId.systemDefault());
            when(clock.instant()).thenReturn(now.atZone(ZoneId.systemDefault()).toInstant());

            Booking booking = Booking.builder().id(UUID.randomUUID()).endAt(futureEnd).build();
            QrCode qrCode = QrCode.builder().id(UUID.randomUUID()).status(QrCodeStatusEnum.ACTIVE).booking(booking).value("x").build();

            QrCode result = service.markExpiredIfBookingEnded(qrCode);

            assertEquals(QrCodeStatusEnum.ACTIVE, result.getStatus());
            verify(qrCodeRepository, never()).saveAndFlush(any());
        }

        @Test
        @DisplayName("does nothing when QR code is already EXPIRED")
        void leavesExpiredWhenAlreadyExpired() {
            LocalDateTime pastEnd = LocalDateTime.of(2025, 2, 1, 14, 0);
            LocalDateTime now = LocalDateTime.of(2025, 2, 15, 12, 0);
            when(clock.getZone()).thenReturn(ZoneId.systemDefault());
            when(clock.instant()).thenReturn(now.atZone(ZoneId.systemDefault()).toInstant());

            Booking booking = Booking.builder().id(UUID.randomUUID()).endAt(pastEnd).build();
            QrCode qrCode = QrCode.builder().id(UUID.randomUUID()).status(QrCodeStatusEnum.EXPIRED).booking(booking).value("x").build();

            QrCode result = service.markExpiredIfBookingEnded(qrCode);

            assertEquals(QrCodeStatusEnum.EXPIRED, result.getStatus());
            verify(qrCodeRepository, never()).saveAndFlush(any());
        }

        @Test
        @DisplayName("returns qrCode unchanged when booking is null")
        void noOpWhenBookingNull() {
            QrCode qrCode = QrCode.builder().id(UUID.randomUUID()).status(QrCodeStatusEnum.ACTIVE).booking(null).value("x").build();

            QrCode result = service.markExpiredIfBookingEnded(qrCode);

            assertEquals(QrCodeStatusEnum.ACTIVE, result.getStatus());
            verify(qrCodeRepository, never()).saveAndFlush(any());
        }
    }
}
