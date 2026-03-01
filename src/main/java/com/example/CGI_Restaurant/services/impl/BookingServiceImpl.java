package com.example.CGI_Restaurant.services.impl;

import com.example.CGI_Restaurant.domain.entities.Booking;
import com.example.CGI_Restaurant.domain.entities.BookingPreference;
import com.example.CGI_Restaurant.domain.entities.BookingTable;
import com.example.CGI_Restaurant.domain.createRequests.CreateBookingPreferenceRequest;
import com.example.CGI_Restaurant.domain.createRequests.CreateBookingRequest;
import com.example.CGI_Restaurant.domain.createRequests.CreateBookingTableRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateBookingPreferenceRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateBookingRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateBookingTableRequest;
import com.example.CGI_Restaurant.exceptions.RestaurantBookingException;
import com.example.CGI_Restaurant.exceptions.notFoundExceptions.BookingNotFoundException;
import com.example.CGI_Restaurant.exceptions.notFoundExceptions.BookingPreferenceNotFoundException;
import com.example.CGI_Restaurant.exceptions.updateException.BookingPreferenceUpdateException;
import com.example.CGI_Restaurant.exceptions.updateException.BookingUpdateException;
import com.example.CGI_Restaurant.repositories.BookingTableRepository;
import com.example.CGI_Restaurant.repositories.UserRepository;
import com.example.CGI_Restaurant.services.EmailService;
import com.example.CGI_Restaurant.services.RestaurantHoursService;
import com.example.CGI_Restaurant.services.TableEntityService;
import com.example.CGI_Restaurant.services.QrCodeService;
import jakarta.persistence.EntityManager;
import com.example.CGI_Restaurant.repositories.BookingRepository;
import com.example.CGI_Restaurant.services.BookingPreferenceService;
import com.example.CGI_Restaurant.services.BookingTableService;
import com.example.CGI_Restaurant.services.BookingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Default implementation of booking operations. Enforces opening hours and booking duration,
 * checks table availability and adjacency for multi-table bookings, creates preferences and
 * booking-table links, generates QR code and sends confirmation email.
 */
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingPreferenceService bookingPreferenceService;
    private final BookingTableService bookingTableService;
    private final BookingTableRepository bookingTableRepository;
    private final EntityManager entityManager;
    private final UserRepository userRepository;
    private final QrCodeService qrCodeService;
    private final EmailService emailService;
    private final RestaurantHoursService restaurantHoursService;
    private final TableEntityService tableEntityService;

    @Override
    @Transactional
    public Booking createBooking(CreateBookingRequest request) {
        if (!restaurantHoursService.isWithinOpeningHours(request.getStartAt(), request.getEndAt())) {
            throw new RestaurantBookingException("Booking time must fall within the restaurant’s opening hours..");
        }
        int durationHours = restaurantHoursService.getBookingDurationHours();
        if (Duration.between(request.getStartAt(), request.getEndAt()).toHours() != durationHours) {
            throw new RestaurantBookingException("The booking duration must be exactly " + durationHours + " hours.");
        }

        var tableRequests = request.getBookingTables() != null ? request.getBookingTables() : List.<CreateBookingTableRequest>of();
        if (!tableRequests.isEmpty()) {
            Set<UUID> requestedTableIds = tableRequests.stream()
                    .map(CreateBookingTableRequest::getTableEntityId)
                    .collect(Collectors.toSet());
            if (requestedTableIds.size() > 1) {
                tableEntityService.validateTablesAdjacent(requestedTableIds);
            }
            List<UUID> bookedInRange = bookingTableRepository.findTableEntityIdsBookedBetween(
                    request.getStartAt(), request.getEndAt());
            boolean anyTaken = requestedTableIds.stream().anyMatch(bookedInRange::contains);
            if (anyTaken) {
                throw new RestaurantBookingException("One or more of the selected tables are already booked at the selected time.");
            }
        }

        Booking booking = new Booking();
        booking.setGuestName(request.getUser().getName());
        booking.setGuestEmail(request.getUser().getEmail());
        booking.setStartAt(request.getStartAt());
        booking.setEndAt(request.getEndAt());
        booking.setPartySize(request.getPartySize());
        booking.setStatus(request.getStatus());
        booking.setQrToken(request.getQrToken() != null ? request.getQrToken() : "");
        booking.setSpecialRequests(request.getSpecialRequests());
        booking.setUser(request.getUser());
        Booking saved = bookingRepository.save(booking);

        var preferences = request.getBookingPreferences() != null ? request.getBookingPreferences() : List.<CreateBookingPreferenceRequest>of();
        for (var pref : preferences) {
            var prefRequest = new CreateBookingPreferenceRequest();
            prefRequest.setBookingId(saved.getId());
            prefRequest.setFeatureId(pref.getFeatureId());
            prefRequest.setPriority(pref.getPriority());
            bookingPreferenceService.create(prefRequest);
        }
        for (var tbl : tableRequests) {
            var tableRequest = new CreateBookingTableRequest();
            tableRequest.setBookingId(saved.getId());
            tableRequest.setTableEntityId(tbl.getTableEntityId());
            bookingTableService.create(tableRequest);
        }
        entityManager.flush();
        entityManager.refresh(saved);

        var qrCode = qrCodeService.generateQrCode(saved);
        saved.getQrCodes().add(qrCode);
        emailService.sendBookingConfirmation(saved, qrCode.getValue());
        return saved;
    }

    @Override
    public Page<Booking> listBookingForCustomer(UUID customerId, Pageable pageable) {
        return bookingRepository.findByUserId(customerId, pageable);
    }

    @Override
    public Page<Booking> listBookingsForAdmin(Pageable pageable) {
        return bookingRepository.findAll(pageable);
    }

    @Override
    public Optional<Booking> getBookingForCustomer(UUID id, UUID customerId) {
        return bookingRepository.findByIdAndUserId(id, customerId);
    }

    @Override
    public Optional<Booking> getBooking(UUID id) {
        return bookingRepository.findById(id);
    }

    @Override
    @Transactional
    public Booking updateBookingForCustomer(UUID id, UUID customerId, UpdateBookingRequest booking) {
        validateUpdateRequest(id, booking);
        Booking existingBooking = bookingRepository
                .findByIdAndUserId(id, customerId)
                .orElseThrow(() -> new BookingNotFoundException(
                        String.format("Booking with ID '%s' does not exist", id)));
        applyBookingFields(existingBooking, booking);
        syncBookingPreferences(existingBooking, booking.getBookingPreferences());
        return bookingRepository.save(existingBooking);
    }

    @Override
    @Transactional
    public Booking updateBooking(UUID id, UpdateBookingRequest booking) {
        validateUpdateRequest(id, booking);
        Booking existingBooking = bookingRepository
                .findById(id)
                .orElseThrow(() -> new BookingNotFoundException(
                        String.format("Booking with ID '%s' does not exist", id)));
        applyBookingFields(existingBooking, booking);
        syncBookingPreferences(existingBooking, booking.getBookingPreferences());
        return bookingRepository.save(existingBooking);
    }

    private void validateUpdateRequest(UUID pathId, UpdateBookingRequest request) {
        if (request.getId() == null) {
            throw new BookingUpdateException("Booking ID cannot be null");
        }
        if (!pathId.equals(request.getId())) {
            throw new BookingUpdateException("Cannot update the ID of a booking");
        }
    }

    private void applyBookingFields(Booking existing, UpdateBookingRequest request) {
        existing.setGuestName(request.getGuestName());
        existing.setGuestEmail(request.getGuestEmail());
        existing.setStartAt(request.getStartAt());
        existing.setEndAt(request.getEndAt());
        existing.setPartySize(request.getPartySize());
        existing.setStatus(request.getStatus());
        existing.setQrToken(request.getQrToken() != null ? request.getQrToken() : "");
        existing.setSpecialRequests(request.getSpecialRequests());
    }

    private void syncBookingPreferences(Booking existingBooking, List<UpdateBookingPreferenceRequest> preferences) {
        List<UpdateBookingPreferenceRequest> preferenceList = preferences != null ? preferences : List.of();
        Set<UUID> requestIds = preferenceList.stream()
                .map(UpdateBookingPreferenceRequest::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        existingBooking.getBookingPreferences().removeIf(p -> !requestIds.contains(p.getId()));

        Map<UUID, BookingPreference> index = existingBooking.getBookingPreferences().stream()
                .collect(Collectors.toMap(BookingPreference::getId, Function.identity()));

        for (UpdateBookingPreferenceRequest req : preferenceList) {
            if (req.getId() == null) {
                BookingPreference newPref = new BookingPreference();
                newPref.setPriority(req.getPriority());
                existingBooking.getBookingPreferences().add(newPref);
            } else if (index.containsKey(req.getId())) {
                index.get(req.getId()).setPriority(req.getPriority());
            } else {
                throw new BookingPreferenceNotFoundException(
                        String.format("Booking preference with ID '%s' does not exist", req.getId()));
            }
        }
    }

    @Override
    @Transactional
    public void deleteBooking(UUID id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(
                        String.format("Booking with ID '%s' does not exist", id)));
        bookingRepository.delete(booking);
    }

    @Override
    @Transactional
    public void deleteBookingForCustomer(UUID id, UUID customerId) {
        Booking booking = bookingRepository.findByIdAndUserId(id, customerId)
                .orElseThrow(() -> new BookingNotFoundException(
                        String.format("Booking with ID '%s' does not exist or does not belong to you", id)));
        bookingRepository.delete(booking);
    }

}
