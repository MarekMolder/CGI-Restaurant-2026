package com.example.CGI_Restaurant.services.impl;

/**
 * Sends booking confirmation emails (HTML body + PDF attachment with QR code) via Spring Mail.
 * Skips sending if spring.mail.username is not set.
 * @author AI (assisted)
 */

import com.example.CGI_Restaurant.domain.entities.Booking;
import com.example.CGI_Restaurant.services.BookingPdfService;
import com.example.CGI_Restaurant.services.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static final String PDF_ATTACHMENT_NAME = "broneeringu-kinnitus.pdf";

    private final JavaMailSender mailSender;
    private final BookingPdfService bookingPdfService;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    @Override
    public void sendBookingConfirmation(Booking booking, String qrCodeImageBase64) {
        if (fromEmail == null || fromEmail.isBlank()) {
            log.warn("Mail not configured (spring.mail.username empty); skipping confirmation email for booking {}", booking.getId());
            return;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(booking.getGuestEmail());
            helper.setSubject("Broneeringu kinnitus – CGI Restaurant");
            helper.setText(buildHtmlBody(booking, qrCodeImageBase64), true);

            byte[] pdfBytes = bookingPdfService.generateBookingPdf(booking, qrCodeImageBase64);
            helper.addAttachment(PDF_ATTACHMENT_NAME, new ByteArrayDataSource(pdfBytes, "application/pdf"));

            mailSender.send(message);
            log.info("Confirmation email with PDF sent for booking {} to {}", booking.getId(), booking.getGuestEmail());
        } catch (MessagingException e) {
            log.error("Failed to send confirmation email for booking {}", booking.getId(), e);
        }
    }

    private String buildHtmlBody(Booking booking, String qrCodeImageBase64) {
        String date = booking.getStartAt().format(DATE_TIME_FORMAT);
        return """
            <h2>Broneeringu kinnitus</h2>
            <p>Tere, %s!</p>
            <p>Teie broneering kuupäevaks <strong>%s</strong> on kinnitatud.</p>
            <p>Kõik detailid ja QR-kood on manustatud PDF-failis – palun vaadake seda ning näidake QR-koodi restoranis kohapeal.</p>
            <p>Ootame teid!<br/>CGI Restaurant</p>
            """
                .formatted(escapeHtml(booking.getGuestName()), date);
    }

    private static String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
