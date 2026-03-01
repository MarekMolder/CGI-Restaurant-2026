package com.example.CGI_Restaurant.controllers;

import com.example.CGI_Restaurant.domain.dtos.ApiErrorResponse;
import com.example.CGI_Restaurant.exceptions.NoMoreTablesException;
import com.example.CGI_Restaurant.exceptions.QrCodeGenerationException;
import com.example.CGI_Restaurant.exceptions.RestaurantBookingException;
import com.example.CGI_Restaurant.exceptions.QrCodeNotFoundException;
import com.example.CGI_Restaurant.exceptions.notFoundExceptions.*;
import com.example.CGI_Restaurant.exceptions.updateException.*;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * Central exception handler for all REST controllers. Maps thrown exceptions to HTTP status codes
 * and a consistent {@link ApiErrorResponse} body so clients always receive a uniform error format.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /** Returns 400 when no tables are available for the requested slot. */
    @ExceptionHandler(NoMoreTablesException.class)
    public ResponseEntity<ApiErrorResponse> handleNoMoreTablesException(NoMoreTablesException ex) {
        log.error("Caught NoMoreTablesException", ex);
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("There are no more tables")
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /** Returns 400 with the exception message when booking rules are violated (e.g. slot, tables). */
    @ExceptionHandler(RestaurantBookingException.class)
    public ResponseEntity<ApiErrorResponse> handleRestaurantBookingException(RestaurantBookingException ex) {
        log.warn("RestaurantBookingException: {}", ex.getMessage());
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage() != null ? ex.getMessage() : "Booking is not possible")
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /** Returns 500 when QR code generation fails. */
    @ExceptionHandler(QrCodeGenerationException.class)
    public ResponseEntity<ApiErrorResponse> handleQrCodeGenerationException(QrCodeGenerationException ex) {
        log.error("Caught QrCodeGenerationException", ex);
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("Unable to generate QR Code")
                .build();
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /** Fallback: returns 500 for any unhandled exception. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(Exception ex) {
        log.error("Caught exception", ex);
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("An unexpected error occurred")
                .build();
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /** Returns 400 with the exception message for invalid arguments. */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /** Returns 409 Conflict for illegal state (e.g. duplicate email on register). */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalStateException(IllegalStateException ex) {
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.CONFLICT.value())
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    /** Returns 401 with a fixed message for failed login (no user/password details exposed). */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .message("Incorrect username or password")
                .build();
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    /** Returns 400 when the user is not found. */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("User not found")
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /** Returns 400 with the first constraint violation message (e.g. @Valid on path/query params). */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        log.error("Caught ConstraintViolationException", ex);

        String errorMessage = ex.getConstraintViolations()
                .stream()
                .findFirst()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage()
                ).orElse("Constraint violation occurred");

        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(errorMessage)
                .build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /** Returns 400 with field errors when request body validation fails (@Valid on DTO). */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        List<org.springframework.validation.FieldError> springFieldErrors = bindingResult.getFieldErrors();
        List<ApiErrorResponse.FieldError> errors = springFieldErrors.stream()
                .map(fe -> ApiErrorResponse.FieldError.builder()
                        .field(fe.getField())
                        .message(fe.getDefaultMessage())
                        .build())
                .toList();
        String firstMessage = springFieldErrors.stream()
                .findFirst()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .orElse("Validation failed");

        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(firstMessage)
                .errors(errors)
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /** Returns 404 when the requested booking does not exist. */
    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleBookingNotFoundException(BookingNotFoundException ex) {
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage() != null ? ex.getMessage() : "Booking not found")
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /** Returns 404 when the requested booking preference does not exist. */
    @ExceptionHandler(BookingPreferenceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleBookingPreferenceNotFoundException(BookingPreferenceNotFoundException ex) {
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage() != null ? ex.getMessage() : "Booking preference not found")
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /** Returns 404 when the requested booking table does not exist. */
    @ExceptionHandler(BookingTableNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleBookingTableNotFoundException(BookingTableNotFoundException ex) {
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage() != null ? ex.getMessage() : "Booking table not found")
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /** Returns 404 when the requested feature does not exist. */
    @ExceptionHandler(FeatureNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleFeatureNotFoundException(FeatureNotFoundException ex) {
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage() != null ? ex.getMessage() : "Feature not found")
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /** Returns 404 when the requested restaurant does not exist. */
    @ExceptionHandler(RestaurantNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleRestaurantNotFoundException(RestaurantNotFoundException ex) {
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage() != null ? ex.getMessage() : "Restaurant not found")
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /** Returns 404 when the requested seating plan does not exist. */
    @ExceptionHandler(SeatingPlanNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleSeatingPlanNotFoundException(SeatingPlanNotFoundException ex) {
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage() != null ? ex.getMessage() : "Seating plan not found")
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /** Returns 404 when the requested table does not exist. */
    @ExceptionHandler(TableEntityNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleTableEntityNotFoundException(TableEntityNotFoundException ex) {
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage() != null ? ex.getMessage() : "Table entity not found")
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /** Returns 404 when the requested menu item does not exist. */
    @ExceptionHandler(MenuItemNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleMenuItemNotFoundException(MenuItemNotFoundException ex) {
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage() != null ? ex.getMessage() : "Menu item not found")
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /** Returns 404 when the requested zone does not exist. */
    @ExceptionHandler(ZoneNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleZoneNotFoundException(ZoneNotFoundException ex) {
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage() != null ? ex.getMessage() : "Zone not found")
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /** Returns 404 when the requested qr code does not exist. */
    @ExceptionHandler(QrCodeNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleQrCodeNotFoundException(QrCodeNotFoundException ex) {
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage() != null ? ex.getMessage() : "Qr Code not found")
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /** Returns 400 when booking update fails (e.g. ID mismatch). */
    @ExceptionHandler(BookingUpdateException.class)
    public ResponseEntity<ApiErrorResponse> handleBookingUpdateException(BookingUpdateException ex) {
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Unable to update booking")
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /** Returns 400 when booking preference update fails (e.g. ID mismatch). */
    @ExceptionHandler(BookingPreferenceUpdateException.class)
    public ResponseEntity<ApiErrorResponse> handleBookingPreferenceUpdateException(BookingPreferenceUpdateException ex) {
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Unable to update booking preference")
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /** Returns 400 when booking table update fails (e.g. ID mismatch). */
    @ExceptionHandler(BookingTableUpdateException.class)
    public ResponseEntity<ApiErrorResponse> handleBookingTableUpdateException(BookingTableUpdateException ex) {
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Unable to update booking table")
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /** Returns 400 when feature update fails (e.g. ID mismatch). */
    @ExceptionHandler(FeatureUpdateException.class)
    public ResponseEntity<ApiErrorResponse> handleFeatureUpdateException(FeatureUpdateException ex) {
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Unable to update feature")
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /** Returns 400 when restaurant update fails (e.g. ID mismatch). */
    @ExceptionHandler(RestaurantUpdateException.class)
    public ResponseEntity<ApiErrorResponse> handleRestaurantUpdateException(RestaurantUpdateException ex) {
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Unable to update restaurant")
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /** Returns 400 when seating plan update fails (e.g. ID mismatch). */
    @ExceptionHandler(SeatingPlanUpdateException.class)
    public ResponseEntity<ApiErrorResponse> handleSeatingPlanUpdateException(SeatingPlanUpdateException ex) {
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Unable to update seating plan")
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /** Returns 400 when table entity update fails (e.g. ID mismatch). */
    @ExceptionHandler(TableEntityUpdateException.class)
    public ResponseEntity<ApiErrorResponse> handleTableEntityUpdateException(TableEntityUpdateException ex) {
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Unable to update table entity")
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /** Returns 400 when zone update fails (e.g. ID mismatch). */
    @ExceptionHandler(ZoneUpdateException.class)
    public ResponseEntity<ApiErrorResponse> handleZoneUpdateException(ZoneUpdateException ex) {
        ApiErrorResponse error = ApiErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Unable to update zone")
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
