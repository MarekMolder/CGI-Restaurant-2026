package com.example.CGI_Restaurant.domain.createRequests;

import com.example.CGI_Restaurant.domain.entities.BookingStatusEnum;
import com.example.CGI_Restaurant.domain.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Incoming data for creating a booking: slot, guest info, optional preferences and table selection. */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateBookingRequest {

    private String guestName;
    private String guestEmail;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private int partySize;
    private BookingStatusEnum status;
    private String qrToken;
    private String specialRequests;
    private User user;
    private List<CreateBookingPreferenceRequest> bookingPreferences = new ArrayList<>();
    private List<CreateBookingTableRequest> bookingTables = new ArrayList<>();
}
