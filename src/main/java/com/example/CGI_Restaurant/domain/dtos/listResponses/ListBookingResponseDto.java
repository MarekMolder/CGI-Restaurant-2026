package com.example.CGI_Restaurant.domain.dtos.listResponses;

import com.example.CGI_Restaurant.domain.dtos.UserInfoDto;
import com.example.CGI_Restaurant.domain.dtos.getResponses.GetBookingPreferenceDetailsResponseDto;
import com.example.CGI_Restaurant.domain.dtos.getResponses.GetBookingTableDetailsResponseDto;
import com.example.CGI_Restaurant.domain.entities.BookingStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListBookingResponseDto {

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
    private List<GetBookingPreferenceDetailsResponseDto> bookingPreferences = new ArrayList<>();
    private List<GetBookingTableDetailsResponseDto> bookingTables = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
