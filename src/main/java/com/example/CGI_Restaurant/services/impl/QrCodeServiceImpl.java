package com.example.CGI_Restaurant.services.impl;

import com.example.CGI_Restaurant.domain.entities.Booking;
import com.example.CGI_Restaurant.domain.entities.QrCode;
import com.example.CGI_Restaurant.domain.entities.QrCodeStatusEnum;
import com.example.CGI_Restaurant.exceptions.QrCodeGenerationException;
import com.example.CGI_Restaurant.repositories.QrCodeRepository;
import com.example.CGI_Restaurant.services.QrCodeService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

/**
 * Generates QR codes (ZXing) for bookings, persists them and can mark them expired after booking end.
 */
@Service
@RequiredArgsConstructor
public class QrCodeServiceImpl implements QrCodeService {

    private static final int QR_HEIGHT = 300;
    private static final int QR_WIDTH = 300;

    private final QRCodeWriter qrCodeWriter;
    private final QrCodeRepository qrCodeRepository;
    private final Clock clock;

    @Override
    public QrCode generateQrCode(Booking booking) {
        try {
            UUID uniqueId = UUID.randomUUID();
            String qrCodeImage = generateQrCodeImage(uniqueId);

            QrCode qrCode = new QrCode();
            qrCode.setId(uniqueId);
            qrCode.setStatus(QrCodeStatusEnum.ACTIVE);
            qrCode.setValue(qrCodeImage);
            qrCode.setBooking(booking);

            return qrCodeRepository.saveAndFlush(qrCode);

        } catch (WriterException | IOException ex) {
            throw new QrCodeGenerationException("Failed to generate QR Code", ex);
        }
    }

    @Override
    public QrCode markExpiredIfBookingEnded(QrCode qrCode) {
        if (qrCode.getBooking() == null) {
            return qrCode;
        }
        LocalDateTime now = LocalDateTime.now(clock);
        if (qrCode.getBooking().getEndAt().isBefore(now) && qrCode.getStatus() == QrCodeStatusEnum.ACTIVE) {
            qrCode.setStatus(QrCodeStatusEnum.EXPIRED);
            return qrCodeRepository.saveAndFlush(qrCode);
        }
        return qrCode;
    }

    private String generateQrCodeImage(UUID uniqueId) throws WriterException, IOException {
        BitMatrix bitMatrix = qrCodeWriter.encode(
                uniqueId.toString(),
                BarcodeFormat.QR_CODE,
                QR_WIDTH,
                QR_HEIGHT
        );

        BufferedImage qrCodeImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        try(ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(qrCodeImage, "PNG", baos);
            byte[] imageBytes = baos.toByteArray();

            return Base64.getEncoder().encodeToString(imageBytes);
        }
    }
}
