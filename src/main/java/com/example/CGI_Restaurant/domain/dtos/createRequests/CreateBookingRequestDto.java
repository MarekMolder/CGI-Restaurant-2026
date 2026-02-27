package com.example.CGI_Restaurant.domain.dtos.createRequests;

import com.example.CGI_Restaurant.domain.entities.BookingStatusEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateBookingRequestDto {

    @jakarta.validation.constraints.AssertTrue(message = "End time must be after start time")
    private boolean isEndAfterStart() {
        if (startAt == null || endAt == null) return true;
        return endAt.isAfter(startAt);
    }

    @NotBlank(message = "Guest name is required")
    @Size(max = 255, message = "Guest name must not exceed 255 characters")
    private String guestName;

    @NotBlank(message = "Guest email is required")
    @Email(message = "Guest email must be a valid email address")
    @Size(max = 255)
    private String guestEmail;

    @NotNull(message = "Start time is required")
    private LocalDateTime startAt;

    @NotNull(message = "End time is required")
    private LocalDateTime endAt;

    @NotNull(message = "Party size is required")
    @jakarta.validation.constraints.Min(value = 1, message = "Party size must be at least 1")
    @jakarta.validation.constraints.Max(value = 50, message = "Party size must not exceed 50")
    private Integer partySize;

    @NotNull(message = "Booking status is required")
    private BookingStatusEnum status;

    @NotBlank(message = "QR token is required")
    @Size(max = 255)
    private String qrToken;

    @Size(max = 2000, message = "Special requests must not exceed 2000 characters")
    private String specialRequests;

    @Valid
    private List<CreateBookingPreferenceRequestDto> bookingPreferences;

    @Valid
    private List<CreateBookingTableRequestDto> bookingTables;
}
