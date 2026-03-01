package com.example.CGI_Restaurant.controllers;

/**
 * @author AI (assisted). Used my BookingControllerTest + AuthControllerTest.
 */

import com.example.CGI_Restaurant.domain.dtos.ApiErrorResponse;
import com.example.CGI_Restaurant.exceptions.*;
import com.example.CGI_Restaurant.exceptions.notFoundExceptions.*;
import com.example.CGI_Restaurant.exceptions.updateException.*;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests that GlobalExceptionHandler maps each exception type to the correct
 * HTTP status and response body.
 */
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Nested
    @DisplayName("Auth and state exceptions")
    class AuthAndState {

        @Test
        @DisplayName("BadCredentialsException -> 401 Unauthorized")
        void badCredentialsReturns401() {
            ResponseEntity<ApiErrorResponse> res = handler.handleBadCredentialsException(
                    new org.springframework.security.authentication.BadCredentialsException("Bad credentials"));

            assertEquals(HttpStatus.UNAUTHORIZED, res.getStatusCode());
            assertNotNull(res.getBody());
            assertEquals(401, res.getBody().getStatus());
            assertEquals("Incorrect username or password", res.getBody().getMessage());
        }

        @Test
        @DisplayName("IllegalStateException -> 409 Conflict")
        void illegalStateReturns409() {
            ResponseEntity<ApiErrorResponse> res = handler.handleIllegalStateException(
                    new IllegalStateException("User already exists"));

            assertEquals(HttpStatus.CONFLICT, res.getStatusCode());
            assertEquals(409, res.getBody().getStatus());
            assertEquals("User already exists", res.getBody().getMessage());
        }

        @Test
        @DisplayName("IllegalArgumentException -> 400 Bad Request")
        void illegalArgumentReturns400() {
            ResponseEntity<ApiErrorResponse> res = handler.handleIllegalArgumentException(
                    new IllegalArgumentException("Invalid id"));

            assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
            assertEquals(400, res.getBody().getStatus());
            assertEquals("Invalid id", res.getBody().getMessage());
        }
    }

    @Nested
    @DisplayName("Business exceptions")
    class BusinessExceptions {

        @Test
        @DisplayName("NoMoreTablesException -> 400")
        void noMoreTablesReturns400() {
            ResponseEntity<ApiErrorResponse> res = handler.handleNoMoreTablesException(new NoMoreTablesException());

            assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
            assertEquals("There are no more tables", res.getBody().getMessage());
        }

        @Test
        @DisplayName("RestaurantBookingException -> 400 with message")
        void restaurantBookingReturns400() {
            ResponseEntity<ApiErrorResponse> res = handler.handleRestaurantBookingException(
                    new RestaurantBookingException("Slot not available"));

            assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
            assertEquals("Slot not available", res.getBody().getMessage());
        }

        @Test
        @DisplayName("QrCodeGenerationException -> 500")
        void qrCodeGenerationReturns500() {
            ResponseEntity<ApiErrorResponse> res = handler.handleQrCodeGenerationException(new QrCodeGenerationException());

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, res.getStatusCode());
            assertEquals("Unable to generate QR Code", res.getBody().getMessage());
        }

        @Test
        @DisplayName("Generic Exception -> 500")
        void genericExceptionReturns500() {
            ResponseEntity<ApiErrorResponse> res = handler.handleException(new RuntimeException("Unexpected"));

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, res.getStatusCode());
            assertEquals("An unexpected error occurred", res.getBody().getMessage());
        }
    }

    @Nested
    @DisplayName("Not found exceptions -> 404")
    class NotFoundExceptions {

        @Test
        @DisplayName("UserNotFoundException -> 400 Bad Request")
        void userNotFound() {
            ResponseEntity<ApiErrorResponse> res = handler.handleUserNotFoundException(new UserNotFoundException());
            assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
            assertEquals("User not found", res.getBody().getMessage());
        }

        @Test
        @DisplayName("BookingNotFoundException uses message or default")
        void bookingNotFound() {
            ResponseEntity<ApiErrorResponse> res = handler.handleBookingNotFoundException(new BookingNotFoundException("Not found"));
            assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
            assertEquals("Not found", res.getBody().getMessage());
        }

        @Test
        @DisplayName("FeatureNotFoundException")
        void featureNotFound() {
            ResponseEntity<ApiErrorResponse> res = handler.handleFeatureNotFoundException(new FeatureNotFoundException());
            assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
            assertEquals("Feature not found", res.getBody().getMessage());
        }

        @Test
        @DisplayName("RestaurantNotFoundException")
        void restaurantNotFound() {
            ResponseEntity<ApiErrorResponse> res = handler.handleRestaurantNotFoundException(new RestaurantNotFoundException());
            assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
        }

        @Test
        @DisplayName("SeatingPlanNotFoundException")
        void seatingPlanNotFound() {
            ResponseEntity<ApiErrorResponse> res = handler.handleSeatingPlanNotFoundException(new SeatingPlanNotFoundException());
            assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
        }

        @Test
        @DisplayName("TableEntityNotFoundException")
        void tableEntityNotFound() {
            ResponseEntity<ApiErrorResponse> res = handler.handleTableEntityNotFoundException(new TableEntityNotFoundException());
            assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
        }

        @Test
        @DisplayName("ZoneNotFoundException")
        void zoneNotFound() {
            ResponseEntity<ApiErrorResponse> res = handler.handleZoneNotFoundException(new ZoneNotFoundException());
            assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
        }

        @Test
        @DisplayName("MenuItemNotFoundException")
        void menuItemNotFound() {
            ResponseEntity<ApiErrorResponse> res = handler.handleMenuItemNotFoundException(new MenuItemNotFoundException("Menu item not found"));
            assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
        }

        @Test
        @DisplayName("BookingPreferenceNotFoundException")
        void bookingPreferenceNotFound() {
            ResponseEntity<ApiErrorResponse> res = handler.handleBookingPreferenceNotFoundException(new BookingPreferenceNotFoundException());
            assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
        }

        @Test
        @DisplayName("BookingTableNotFoundException")
        void bookingTableNotFound() {
            ResponseEntity<ApiErrorResponse> res = handler.handleBookingTableNotFoundException(new BookingTableNotFoundException());
            assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
        }

        @Test
        @DisplayName("QrCodeNotFoundException")
        void qrCodeNotFound() {
            ResponseEntity<ApiErrorResponse> res = handler.handleQrCodeNotFoundException(new QrCodeNotFoundException());
            assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
            assertEquals("Qr Code not found", res.getBody().getMessage());
        }
    }

    @Nested
    @DisplayName("Update exceptions -> 400")
    class UpdateExceptions {

        @Test
        @DisplayName("BookingUpdateException")
        void bookingUpdate() {
            ResponseEntity<ApiErrorResponse> res = handler.handleBookingUpdateException(new BookingUpdateException());
            assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
            assertEquals("Unable to update booking", res.getBody().getMessage());
        }

        @Test
        @DisplayName("BookingPreferenceUpdateException")
        void bookingPreferenceUpdate() {
            ResponseEntity<ApiErrorResponse> res = handler.handleBookingPreferenceUpdateException(new BookingPreferenceUpdateException());
            assertEquals("Unable to update booking preference", res.getBody().getMessage());
        }

        @Test
        @DisplayName("BookingTableUpdateException")
        void bookingTableUpdate() {
            ResponseEntity<ApiErrorResponse> res = handler.handleBookingTableUpdateException(new BookingTableUpdateException());
            assertEquals("Unable to update booking table", res.getBody().getMessage());
        }

        @Test
        @DisplayName("FeatureUpdateException")
        void featureUpdate() {
            ResponseEntity<ApiErrorResponse> res = handler.handleFeatureUpdateException(new FeatureUpdateException());
            assertEquals("Unable to update feature", res.getBody().getMessage());
        }

        @Test
        @DisplayName("RestaurantUpdateException")
        void restaurantUpdate() {
            ResponseEntity<ApiErrorResponse> res = handler.handleRestaurantUpdateException(new RestaurantUpdateException());
            assertEquals("Unable to update restaurant", res.getBody().getMessage());
        }

        @Test
        @DisplayName("SeatingPlanUpdateException")
        void seatingPlanUpdate() {
            ResponseEntity<ApiErrorResponse> res = handler.handleSeatingPlanUpdateException(new SeatingPlanUpdateException());
            assertEquals("Unable to update seating plan", res.getBody().getMessage());
        }

        @Test
        @DisplayName("TableEntityUpdateException")
        void tableEntityUpdate() {
            ResponseEntity<ApiErrorResponse> res = handler.handleTableEntityUpdateException(new TableEntityUpdateException());
            assertEquals("Unable to update table entity", res.getBody().getMessage());
        }

        @Test
        @DisplayName("ZoneUpdateException")
        void zoneUpdate() {
            ResponseEntity<ApiErrorResponse> res = handler.handleZoneUpdateException(new ZoneUpdateException());
            assertEquals("Unable to update zone", res.getBody().getMessage());
        }
    }

    @Nested
    @DisplayName("Validation exceptions")
    class ValidationExceptions {

        /** Target bean for MethodArgumentNotValidException must have readable properties for rejectValue. */
        @SuppressWarnings("unused")
        private static class ValidatableTarget {
            private String email;
            private String password;
            public String getEmail() { return email; }
            public void setEmail(String email) { this.email = email; }
            public String getPassword() { return password; }
            public void setPassword(String password) { this.password = password; }
        }

        @Test
        @DisplayName("MethodArgumentNotValidException -> 400 with field errors")
        void methodArgumentNotValidReturns400() {
            BindingResult bindingResult = new BeanPropertyBindingResult(new ValidatableTarget(), "target");
            bindingResult.rejectValue("email", "NotBlank", "Email is required");
            bindingResult.rejectValue("password", "Size", "Password must be 6-100 chars");
            MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

            ResponseEntity<ApiErrorResponse> res = handler.handleMethodArgumentNotValidException(ex);

            assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
            assertEquals(400, res.getBody().getStatus());
            assertTrue(res.getBody().getMessage().contains("email") || res.getBody().getMessage().contains("Email"));
            assertNotNull(res.getBody().getErrors());
            assertEquals(2, res.getBody().getErrors().size());
        }

        @Test
        @DisplayName("ConstraintViolationException -> 400 with violation message")
        void constraintViolationReturns400() {
            Path.Node node = mock(Path.Node.class);
            when(node.getName()).thenReturn("fieldName");
            Path path = mock(Path.class);
            when(path.iterator()).thenReturn(Collections.singletonList(node).iterator());
            ConstraintViolation<?> violation = mock(ConstraintViolation.class);
            when(violation.getMessage()).thenReturn("must not be null");
            when(violation.getPropertyPath()).thenReturn(path);

            Set<ConstraintViolation<?>> violations = Set.of(violation);
            ConstraintViolationException ex = new ConstraintViolationException("validation failed", violations);

            ResponseEntity<ApiErrorResponse> res = handler.handleConstraintViolationException(ex);

            assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
            assertNotNull(res.getBody().getMessage());
            assertTrue(res.getBody().getMessage().contains("fieldName") || res.getBody().getMessage().contains("must not be null"));
        }
    }
}
