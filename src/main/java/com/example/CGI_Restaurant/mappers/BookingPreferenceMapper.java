package com.example.CGI_Restaurant.mappers;

import com.example.CGI_Restaurant.domain.dtos.requests.CreateBookingPreferenceRequestDto;
import com.example.CGI_Restaurant.domain.dtos.responses.CreateBookingPreferenceResponseDto;
import com.example.CGI_Restaurant.domain.entities.BookingPreference;
import com.example.CGI_Restaurant.domain.requests.CreateBookingPreferenceRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookingPreferenceMapper {
    CreateBookingPreferenceRequest fromDto(CreateBookingPreferenceRequestDto dto);

    CreateBookingPreferenceResponseDto toDto(BookingPreference bookingPreference);
}
