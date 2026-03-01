package com.example.CGI_Restaurant.mappers;

import com.example.CGI_Restaurant.domain.dtos.getResponses.GetBookingPreferenceDetailsResponseDto;
import com.example.CGI_Restaurant.domain.dtos.listResponses.ListBookingPreferenceResponseDto;
import com.example.CGI_Restaurant.domain.dtos.createRequests.CreateBookingPreferenceRequestDto;
import com.example.CGI_Restaurant.domain.dtos.createResponses.CreateBookingPreferenceResponseDto;
import com.example.CGI_Restaurant.domain.dtos.updateRequests.UpdateBookingPreferenceRequestDto;
import com.example.CGI_Restaurant.domain.dtos.updateResponses.UpdateBookingPreferenceResponseDto;
import com.example.CGI_Restaurant.domain.entities.BookingPreference;
import com.example.CGI_Restaurant.domain.createRequests.CreateBookingPreferenceRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateBookingPreferenceRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateBookingRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper between BookingPreference entity and create/update/list/get DTOs.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookingPreferenceMapper {

    CreateBookingPreferenceRequest fromDto(CreateBookingPreferenceRequestDto dto);
    CreateBookingPreferenceResponseDto toDto(BookingPreference bookingPreference);
    ListBookingPreferenceResponseDto toListBookingPreferenceResponseDto(BookingPreference bookingPreference);
    GetBookingPreferenceDetailsResponseDto toGetBookingPreferenceDetailsResponseDto(BookingPreference bookingPreference);
    UpdateBookingPreferenceRequest fromDto(UpdateBookingPreferenceRequestDto dto);
    UpdateBookingPreferenceResponseDto toUpdateBookingPreferenceResponseDto(BookingPreference bookingPreference);
}
