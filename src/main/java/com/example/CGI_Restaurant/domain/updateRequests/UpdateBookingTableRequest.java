package com.example.CGI_Restaurant.domain.updateRequests;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/** Request to update a booking-table link (id only; used for merge/sync). */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBookingTableRequest {
    private UUID id;
}
