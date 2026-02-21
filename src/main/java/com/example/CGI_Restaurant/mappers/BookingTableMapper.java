package com.example.CGI_Restaurant.mappers;

import com.example.CGI_Restaurant.domain.dtos.requests.CreateBookingTableRequestDto;
import com.example.CGI_Restaurant.domain.dtos.responses.CreateBookingTableResponseDto;
import com.example.CGI_Restaurant.domain.entities.BookingTable;
import com.example.CGI_Restaurant.domain.requests.CreateBookingTableRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookingTableMapper {
    CreateBookingTableRequest fromDto(CreateBookingTableRequestDto dto);

    CreateBookingTableResponseDto toDto(BookingTable bookingTable);
}
