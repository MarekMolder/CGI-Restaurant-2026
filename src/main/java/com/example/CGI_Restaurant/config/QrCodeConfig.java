package com.example.CGI_Restaurant.config;

import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * Configuration for QR code generation and time-dependent logic.
 */
@Configuration
public class QrCodeConfig {

    /** ZXing QR code writer used to generate booking QR codes. */
    @Bean
    public QRCodeWriter qrCodeWriter() {
        return new QRCodeWriter();
    }

    /** Clock for current time; allows overriding in tests. */
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
