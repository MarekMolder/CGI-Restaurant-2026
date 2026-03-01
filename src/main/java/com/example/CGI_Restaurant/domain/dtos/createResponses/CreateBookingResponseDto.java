package com.example.CGI_Restaurant.domain.dtos.createResponses;

import com.example.CGI_Restaurant.domain.dtos.UserInfoDto;
import com.example.CGI_Restaurant.domain.entities.BookingStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
/** API response after creating a booking (includes optional QR code image). */
@AllArgsConstructor
@NoArgsConstructor
public class CreateBookingResponseDto {
    private UUID id;
    private String guestName;
    private String guestEmail;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private int partySize;
    private BookingStatusEnum status;
    private String qrToken;
    private String qrCodeImageBase64;
    private String specialRequests;
    private UserInfoDto user;
    private List<CreateBookingPreferenceResponseDto> bookingPreferences = new ArrayList<>();
    private List<CreateBookingTableResponseDto> bookingTables = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
