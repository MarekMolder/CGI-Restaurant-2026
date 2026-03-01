package com.example.CGI_Restaurant.mappers;

import com.example.CGI_Restaurant.domain.dtos.getResponses.GetBookingDetailsResponseDto;
import com.example.CGI_Restaurant.domain.dtos.listResponses.ListBookingResponseDto;
import com.example.CGI_Restaurant.domain.dtos.createRequests.CreateBookingRequestDto;
import com.example.CGI_Restaurant.domain.dtos.createResponses.CreateBookingResponseDto;
import com.example.CGI_Restaurant.domain.dtos.updateRequests.UpdateBookingRequestDto;
import com.example.CGI_Restaurant.domain.dtos.updateResponses.UpdateBookingResponseDto;
import com.example.CGI_Restaurant.domain.entities.Booking;
import com.example.CGI_Restaurant.domain.createRequests.CreateBookingRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateBookingRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper between Booking entity, create/update request DTOs and response DTOs. Uses UserMapper for user info.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = UserMapper.class)
public interface BookingMapper {

    CreateBookingRequest fromDto(CreateBookingRequestDto dto);
    CreateBookingResponseDto toDto(Booking booking);
    ListBookingResponseDto toListBookingResponseDto(Booking booking);
    GetBookingDetailsResponseDto toGetBookingDetailsResponseDto(Booking booking);
    UpdateBookingRequest fromDto(UpdateBookingRequestDto dto);
    UpdateBookingResponseDto toUpdateBookingResponseDto(Booking booking);
}
