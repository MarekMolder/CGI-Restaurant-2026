package com.example.CGI_Restaurant.services.impl;

/**
 * @author AI (assisted). Used my BookingSeviceImplTest.
 */

import com.example.CGI_Restaurant.domain.dtos.listResponses.MenuItemResponseDto;
import com.example.CGI_Restaurant.domain.entities.MenuItem;
import com.example.CGI_Restaurant.domain.entities.Restaurant;
import com.example.CGI_Restaurant.domain.createRequests.CreateMenuItemRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateMenuItemRequest;
import com.example.CGI_Restaurant.exceptions.notFoundExceptions.MenuItemNotFoundException;
import com.example.CGI_Restaurant.exceptions.notFoundExceptions.RestaurantNotFoundException;
import com.example.CGI_Restaurant.mappers.MenuItemMapper;
import com.example.CGI_Restaurant.repositories.MenuItemRepository;
import com.example.CGI_Restaurant.repositories.RestaurantRepository;
import com.example.CGI_Restaurant.services.TheMealDBService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuItemServiceImplTest {

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private MenuItemMapper menuItemMapper;

    @Mock
    private TheMealDBService theMealDBService;

    @InjectMocks
    private MenuItemServiceImpl service;

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("saves new menu item when restaurant exists")
        void createsWhenRestaurantExists() {
            UUID restaurantId = UUID.randomUUID();
            Restaurant restaurant = Restaurant.builder().id(restaurantId).name("Resto Aed").timezone("Europe/Tallinn")
                    .email("info@restoaed.ee").phone("+372 6123456").address("Pärnu mnt 1").build();

            CreateMenuItemRequest request = new CreateMenuItemRequest();
            request.setRestaurantId(restaurantId);
            request.setName("Caesar salat");
            request.setDescription("Roheline salat Caesar kastmega");
            request.setPriceEur(new BigDecimal("8.50"));
            request.setCategory("Eelroog");
            request.setImageUrl(null);

            MenuItem saved = MenuItem.builder().id(UUID.randomUUID()).name("Caesar salat").priceEur(new BigDecimal("8.50")).restaurant(restaurant).build();
            when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
            when(menuItemRepository.save(any(MenuItem.class))).thenReturn(saved);

            MenuItem result = service.create(request);

            assertNotNull(result);
            assertEquals("Caesar salat", result.getName());
            ArgumentCaptor<MenuItem> captor = ArgumentCaptor.forClass(MenuItem.class);
            verify(menuItemRepository).save(captor.capture());
            assertEquals(restaurantId, captor.getValue().getRestaurant().getId());
        }

        @Test
        @DisplayName("throws RestaurantNotFoundException when restaurant not found")
        void throwsWhenRestaurantNotFound() {
            UUID restaurantId = UUID.randomUUID();
            CreateMenuItemRequest request = new CreateMenuItemRequest();
            request.setRestaurantId(restaurantId);
            request.setName("Salat");
            when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

            assertThrows(RestaurantNotFoundException.class, () -> service.create(request));
            verify(menuItemRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("listByRestaurantId")
    class ListByRestaurantId {

        @Test
        @DisplayName("returns list of DTOs mapped from repository")
        void returnsMappedList() {
            UUID restaurantId = UUID.randomUUID();
            MenuItem item = MenuItem.builder().id(UUID.randomUUID()).name("Sealihapada").priceEur(new BigDecimal("14.90")).build();
            MenuItemResponseDto dto = new MenuItemResponseDto();
            dto.setName("Sealihapada");
            when(menuItemRepository.findByRestaurantIdOrderByCategoryAscNameAsc(restaurantId)).thenReturn(List.of(item));
            when(menuItemMapper.toMenuItemResponseDto(item)).thenReturn(dto);

            List<MenuItemResponseDto> result = service.listByRestaurantId(restaurantId);

            assertEquals(1, result.size());
            assertEquals("Sealihapada", result.get(0).getName());
        }

        @Test
        @DisplayName("returns empty list when no items")
        void returnsEmptyWhenNoItems() {
            when(menuItemRepository.findByRestaurantIdOrderByCategoryAscNameAsc(any(UUID.class))).thenReturn(List.of());
            assertTrue(service.listByRestaurantId(UUID.randomUUID()).isEmpty());
        }
    }

    @Nested
    @DisplayName("getById")
    class GetById {

        @Test
        @DisplayName("returns present when found")
        void returnsPresentWhenFound() {
            UUID id = UUID.randomUUID();
            MenuItem item = MenuItem.builder().id(id).name("Šokolaadikook").build();
            when(menuItemRepository.findById(id)).thenReturn(Optional.of(item));
            assertTrue(service.getById(id).isPresent());
            assertEquals("Šokolaadikook", service.getById(id).get().getName());
        }

        @Test
        @DisplayName("returns empty when not found")
        void returnsEmptyWhenNotFound() {
            when(menuItemRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
            assertTrue(service.getById(UUID.randomUUID()).isEmpty());
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("updates only non-null fields and saves")
        void updatesNonNullFields() {
            UUID id = UUID.randomUUID();
            UpdateMenuItemRequest request = new UpdateMenuItemRequest();
            request.setName("Caesar salat uuendatud");
            request.setPriceEur(new BigDecimal("9.00"));
            request.setDescription(null);
            request.setCategory(null);
            request.setImageUrl(null);

            MenuItem existing = MenuItem.builder().id(id).name("Caesar salat").description("Vanakastme").priceEur(new BigDecimal("8.50")).category("Eelroog").build();
            when(menuItemRepository.findById(id)).thenReturn(Optional.of(existing));
            when(menuItemRepository.save(any(MenuItem.class))).thenAnswer(inv -> inv.getArgument(0));

            MenuItem result = service.update(id, request);

            assertEquals("Caesar salat uuendatud", result.getName());
            assertEquals(new BigDecimal("9.00"), result.getPriceEur());
            verify(menuItemRepository).save(existing);
        }

        @Test
        @DisplayName("throws MenuItemNotFoundException when not found")
        void throwsWhenNotFound() {
            UUID id = UUID.randomUUID();
            UpdateMenuItemRequest request = new UpdateMenuItemRequest();
            request.setName("Nimi");
            when(menuItemRepository.findById(id)).thenReturn(Optional.empty());

            assertThrows(MenuItemNotFoundException.class, () -> service.update(id, request));
            verify(menuItemRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("deletes when exists")
        void deletesWhenExists() {
            UUID id = UUID.randomUUID();
            MenuItem item = MenuItem.builder().id(id).name("Praetine soolane").build();
            when(menuItemRepository.findById(id)).thenReturn(Optional.of(item));
            service.delete(id);
            verify(menuItemRepository).delete(item);
        }

        @Test
        @DisplayName("throws MenuItemNotFoundException when not found")
        void throwsWhenNotFound() {
            UUID id = UUID.randomUUID();
            when(menuItemRepository.findById(id)).thenReturn(Optional.empty());
            assertThrows(MenuItemNotFoundException.class, () -> service.delete(id));
            verify(menuItemRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("addFromTheMealDB")
    class AddFromTheMealDB {

        @Test
        @DisplayName("delegates to TheMealDBService and returns menu item when restaurant exists")
        void delegatesWhenRestaurantExists() {
            UUID restaurantId = UUID.randomUUID();
            Restaurant restaurant = Restaurant.builder().id(restaurantId).name("Resto").timezone("UTC").email("e@e.ee").phone("1").address("A").build();
            MenuItem imported = MenuItem.builder().id(UUID.randomUUID()).name("TheMealDB meal").priceEur(new BigDecimal("12.00")).restaurant(restaurant).build();

            when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
            when(theMealDBService.importMealAsMenuItem(eq(restaurant), eq("52772"), any(BigDecimal.class))).thenReturn(imported);

            MenuItem result = service.addFromTheMealDB(restaurantId, "52772", new BigDecimal("12.00"));

            assertEquals("TheMealDB meal", result.getName());
            verify(theMealDBService).importMealAsMenuItem(restaurant, "52772", new BigDecimal("12.00"));
        }

        @Test
        @DisplayName("throws RestaurantNotFoundException when restaurant not found")
        void throwsWhenRestaurantNotFound() {
            UUID restaurantId = UUID.randomUUID();
            when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

            assertThrows(RestaurantNotFoundException.class, () -> service.addFromTheMealDB(restaurantId, "52772", new BigDecimal("10.00")));
            verify(theMealDBService, never()).importMealAsMenuItem(any(), any(), any());
        }
    }
}
