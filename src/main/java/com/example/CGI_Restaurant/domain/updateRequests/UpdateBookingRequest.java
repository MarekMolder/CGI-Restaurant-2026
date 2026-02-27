package com.example.CGI_Restaurant.domain.updateRequests;

import com.example.CGI_Restaurant.domain.createRequests.CreateBookingPreferenceRequest;
import com.example.CGI_Restaurant.domain.createRequests.CreateBookingTableRequest;
import com.example.CGI_Restaurant.domain.entities.BookingStatusEnum;
import com.example.CGI_Restaurant.domain.entities.User;
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
public class UpdateBookingRequest {
    private UUID id;
    private String guestName;
    private String guestEmail;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private int partySize;
    private BookingStatusEnum status;
    private String qrToken;
    private String specialRequests;
    private User user;
    private List<UpdateBookingPreferenceRequest> bookingPreferences = new ArrayList<>();
    private List<UpdateBookingTableRequest> bookingTables = new ArrayList<>();
}
