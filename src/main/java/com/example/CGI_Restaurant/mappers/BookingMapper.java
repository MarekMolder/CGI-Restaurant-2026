package com.example.CGI_Restaurant.mappers;

import com.example.CGI_Restaurant.domain.dtos.requests.CreateBookingRequestDto;
import com.example.CGI_Restaurant.domain.dtos.responses.CreateBookingResponseDto;
import com.example.CGI_Restaurant.domain.entities.Booking;
import com.example.CGI_Restaurant.domain.requests.CreateBookingRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = UserMapper.class)
public interface BookingMapper {

    CreateBookingRequest fromDto(CreateBookingRequestDto dto);

    CreateBookingResponseDto toDto(Booking booking);
}
