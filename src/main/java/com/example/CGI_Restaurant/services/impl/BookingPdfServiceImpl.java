package com.example.CGI_Restaurant.services.impl;

import com.example.CGI_Restaurant.domain.entities.Booking;
import com.example.CGI_Restaurant.services.BookingPdfService;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Service
@Slf4j
public class BookingPdfServiceImpl implements BookingPdfService {

    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static final float QR_IMAGE_SIZE = 150f;

    @Override
    public byte[] generateBookingPdf(Booking booking, String qrCodeImageBase64) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();

            document.add(new Paragraph("Broneeringu kinnitus – CGI Restaurant"));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Tere, " + booking.getGuestName() + "!"));
            document.add(new Paragraph("Teie broneering on kinnitatud."));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Algus: " + booking.getStartAt().format(DATE_TIME_FORMAT)));
            document.add(new Paragraph("Lõpp: " + booking.getEndAt().format(DATE_TIME_FORMAT)));
            document.add(new Paragraph("Inimeste arv: " + booking.getPartySize()));
            document.add(new Paragraph("Broneeringu ID: " + booking.getId()));
            document.add(new Paragraph(" "));

            if (qrCodeImageBase64 != null && !qrCodeImageBase64.isBlank()) {
                byte[] imageBytes = Base64.getDecoder().decode(qrCodeImageBase64);
                Image img = Image.getInstance(imageBytes);
                img.scaleToFit(QR_IMAGE_SIZE, QR_IMAGE_SIZE);
                document.add(img);
                document.add(new Paragraph(" "));
            }

            document.add(new Paragraph("Palun näidake seda QR-koodi restoranis kohapeal."));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Parimate soovidega, CGI Restaurant"));

            document.close();
            return baos.toByteArray();
        } catch (DocumentException e) {
            log.error("Failed to generate booking PDF", e);
            throw new RuntimeException("PDF genereerimine ebaõnnestus", e);
        } catch (Exception e) {
            log.error("Failed to generate booking PDF", e);
            throw new RuntimeException("PDF genereerimine ebaõnnestus", e);
        }
    }
}
