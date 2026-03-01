package com.example.CGI_Restaurant.mappers;

import com.example.CGI_Restaurant.domain.dtos.getResponses.GetBookingTableDetailsResponseDto;
import com.example.CGI_Restaurant.domain.dtos.listResponses.ListBookingTableResponseDto;
import com.example.CGI_Restaurant.domain.dtos.createRequests.CreateBookingTableRequestDto;
import com.example.CGI_Restaurant.domain.dtos.createResponses.CreateBookingTableResponseDto;
import com.example.CGI_Restaurant.domain.dtos.updateRequests.UpdateBookingTableRequestDto;
import com.example.CGI_Restaurant.domain.dtos.updateResponses.UpdateBookingResponseDto;
import com.example.CGI_Restaurant.domain.dtos.updateResponses.UpdateBookingTableResponseDto;
import com.example.CGI_Restaurant.domain.entities.BookingTable;
import com.example.CGI_Restaurant.domain.createRequests.CreateBookingTableRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateBookingTableRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper between BookingTable entity and create/update/list/get DTOs.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookingTableMapper {

    CreateBookingTableRequest fromDto(CreateBookingTableRequestDto dto);
    CreateBookingTableResponseDto toDto(BookingTable bookingTable);
    ListBookingTableResponseDto toListBookingTableResponseDto(BookingTable bookingTable);
    GetBookingTableDetailsResponseDto toGetBookingTableDetailsResponseDto(BookingTable bookingTable);
    UpdateBookingTableRequest fromDto(UpdateBookingTableRequestDto dto);
    UpdateBookingTableResponseDto toUpdateBookingTableResponseDto(BookingTable bookingTable);
}
