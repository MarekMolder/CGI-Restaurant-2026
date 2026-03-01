package com.example.CGI_Restaurant.services.impl;

/**
 * @author AI (assisted). Used my BookingSeviceImplTest.
 */

import com.example.CGI_Restaurant.domain.entities.Restaurant;
import com.example.CGI_Restaurant.domain.createRequests.CreateRestaurantRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateRestaurantRequest;
import com.example.CGI_Restaurant.exceptions.notFoundExceptions.RestaurantNotFoundException;
import com.example.CGI_Restaurant.exceptions.updateException.RestaurantUpdateException;
import com.example.CGI_Restaurant.repositories.RestaurantRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceImplTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private RestaurantServiceImpl service;

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("saves new restaurant with request data and returns saved entity")
        void createsRestaurantWithRequestData() {
            CreateRestaurantRequest request = new CreateRestaurantRequest();
            request.setName("Resto Aed");
            request.setTimezone("Europe/Tallinn");
            request.setEmail("info@restoaed.ee");
            request.setPhone("+372 6123456");
            request.setAddress("Pärnu mnt 1, Tallinn");

            Restaurant saved = Restaurant.builder()
                    .id(UUID.randomUUID())
                    .name("Resto Aed")
                    .timezone("Europe/Tallinn")
                    .email("info@restoaed.ee")
                    .phone("+372 6123456")
                    .address("Pärnu mnt 1, Tallinn")
                    .build();
            when(restaurantRepository.save(any(Restaurant.class))).thenReturn(saved);

            Restaurant result = service.create(request);

            assertNotNull(result);
            assertEquals("Resto Aed", result.getName());
            assertEquals("info@restoaed.ee", result.getEmail());
            ArgumentCaptor<Restaurant> captor = ArgumentCaptor.forClass(Restaurant.class);
            verify(restaurantRepository).save(captor.capture());
            assertEquals("Resto Aed", captor.getValue().getName());
            assertEquals("Europe/Tallinn", captor.getValue().getTimezone());
        }
    }

    @Nested
    @DisplayName("list")
    class ListMethod {

        @Test
        @DisplayName("returns page from repository")
        void returnsPageFromRepository() {
            Pageable pageable = PageRequest.of(0, 10);
            List<Restaurant> content = List.of(
                    Restaurant.builder().id(UUID.randomUUID()).name("Resto Aed").timezone("Europe/Tallinn")
                            .email("a@a.ee").phone("1").address("A").build()
            );
            Page<Restaurant> page = new PageImpl<>(content, pageable, 1);
            when(restaurantRepository.findAll(pageable)).thenReturn(page);

            Page<Restaurant> result = service.list(pageable);

            assertEquals(1, result.getContent().size());
            assertEquals("Resto Aed", result.getContent().get(0).getName());
            verify(restaurantRepository).findAll(pageable);
        }
    }

    @Nested
    @DisplayName("getById")
    class GetById {

        @Test
        @DisplayName("returns optional with restaurant when found")
        void returnsPresentWhenFound() {
            UUID id = UUID.randomUUID();
            Restaurant restaurant = Restaurant.builder().id(id).name("Vana Kelder").timezone("Europe/Tallinn")
                    .email("v@v.ee").phone("2").address("B").build();
            when(restaurantRepository.findById(id)).thenReturn(Optional.of(restaurant));

            Optional<Restaurant> result = service.getById(id);

            assertTrue(result.isPresent());
            assertEquals(id, result.get().getId());
            assertEquals("Vana Kelder", result.get().getName());
        }

        @Test
        @DisplayName("returns empty optional when not found")
        void returnsEmptyWhenNotFound() {
            UUID id = UUID.randomUUID();
            when(restaurantRepository.findById(id)).thenReturn(Optional.empty());

            Optional<Restaurant> result = service.getById(id);

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("updates and returns entity when id matches")
        void updatesWhenIdMatches() {
            UUID id = UUID.randomUUID();
            UpdateRestaurantRequest request = new UpdateRestaurantRequest();
            request.setId(id);
            request.setName("Resto Aed uuendatud");
            request.setTimezone("Europe/Tallinn");
            request.setEmail("uuendatud@restoaed.ee");
            request.setPhone("+372 5550000");
            request.setAddress("Narva mnt 5, Tallinn");

            Restaurant existing = Restaurant.builder().id(id).name("Resto Aed").timezone("Europe/Tallinn")
                    .email("old@ee").phone("1").address("Old").build();
            when(restaurantRepository.findById(id)).thenReturn(Optional.of(existing));
            when(restaurantRepository.save(any(Restaurant.class))).thenAnswer(inv -> inv.getArgument(0));

            Restaurant result = service.update(id, request);

            assertEquals("Resto Aed uuendatud", result.getName());
            assertEquals("uuendatud@restoaed.ee", result.getEmail());
            verify(restaurantRepository).save(existing);
        }

        @Test
        @DisplayName("throws RestaurantUpdateException when request id is null")
        void throwsWhenRequestIdNull() {
            UUID id = UUID.randomUUID();
            UpdateRestaurantRequest request = new UpdateRestaurantRequest();
            request.setId(null);
            request.setName("Nimi");

            assertThrows(RestaurantUpdateException.class, () -> service.update(id, request));
            verify(restaurantRepository, never()).save(any());
        }

        @Test
        @DisplayName("throws RestaurantUpdateException when path id and request id differ")
        void throwsWhenIdMismatch() {
            UUID pathId = UUID.randomUUID();
            UUID requestId = UUID.randomUUID();
            UpdateRestaurantRequest request = new UpdateRestaurantRequest();
            request.setId(requestId);
            request.setName("Nimi");

            assertThrows(RestaurantUpdateException.class, () -> service.update(pathId, request));
            verify(restaurantRepository, never()).save(any());
        }

        @Test
        @DisplayName("throws RestaurantNotFoundException when restaurant not found")
        void throwsWhenNotFound() {
            UUID id = UUID.randomUUID();
            UpdateRestaurantRequest request = new UpdateRestaurantRequest();
            request.setId(id);
            request.setName("Nimi");
            when(restaurantRepository.findById(id)).thenReturn(Optional.empty());

            assertThrows(RestaurantNotFoundException.class, () -> service.update(id, request));
            verify(restaurantRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("deletes when restaurant exists")
        void deletesWhenExists() {
            UUID id = UUID.randomUUID();
            Restaurant restaurant = Restaurant.builder().id(id).name("Resto").timezone("UTC")
                    .email("e@e.ee").phone("1").address("A").build();
            when(restaurantRepository.findById(id)).thenReturn(Optional.of(restaurant));

            service.delete(id);

            verify(restaurantRepository).delete(restaurant);
        }

        @Test
        @DisplayName("throws RestaurantNotFoundException when restaurant not found")
        void throwsWhenNotFound() {
            UUID id = UUID.randomUUID();
            when(restaurantRepository.findById(id)).thenReturn(Optional.empty());

            assertThrows(RestaurantNotFoundException.class, () -> service.delete(id));
            verify(restaurantRepository, never()).delete(any());
        }
    }
}
