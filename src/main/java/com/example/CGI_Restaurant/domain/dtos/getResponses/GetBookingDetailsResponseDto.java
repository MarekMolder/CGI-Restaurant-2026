package com.example.CGI_Restaurant.domain.dtos.getResponses;

import com.example.CGI_Restaurant.domain.dtos.UserInfoDto;
import com.example.CGI_Restaurant.domain.dtos.listResponses.ListBookingPreferenceResponseDto;
import com.example.CGI_Restaurant.domain.dtos.listResponses.ListBookingTableResponseDto;
import com.example.CGI_Restaurant.domain.entities.BookingStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
/** API response for a single booking (detail view). */
@AllArgsConstructor
@NoArgsConstructor
public class GetBookingDetailsResponseDto {
    private UUID id;
    private String guestName;
    private String guestEmail;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private int partySize;
    private BookingStatusEnum status;
    private String qrToken;
    private String specialRequests;
    private UserInfoDto user;
    private List<ListBookingPreferenceResponseDto> bookingPreferences = new ArrayList<>();
    private List<ListBookingTableResponseDto> bookingTables = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
