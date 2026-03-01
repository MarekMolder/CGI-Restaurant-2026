package com.example.CGI_Restaurant.mappers;

/**
 * @author AI (assisted). Used my BookingMapperTest + UserMapperTest.
 */

import com.example.CGI_Restaurant.domain.dtos.createRequests.CreateMenuItemRequestDto;
import com.example.CGI_Restaurant.domain.dtos.listResponses.MenuItemResponseDto;
import com.example.CGI_Restaurant.domain.dtos.updateRequests.UpdateMenuItemRequestDto;
import com.example.CGI_Restaurant.domain.entities.MenuItem;
import com.example.CGI_Restaurant.domain.createRequests.CreateMenuItemRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateMenuItemRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Import(com.example.CGI_Restaurant.TestMailConfig.class)
class MenuItemMapperTest {

    @Autowired
    private MenuItemMapper menuItemMapper;

    private static MenuItem createMenuItem(UUID id, String name, BigDecimal price) {
        LocalDateTime now = LocalDateTime.now();
        return MenuItem.builder()
                .id(id)
                .name(name)
                .description("Tasty dish")
                .priceEur(price)
                .category("Main")
                .imageUrl(null)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    @Nested
    @DisplayName("fromDto CreateMenuItemRequestDto")
    class FromDtoCreate {

        @Test
        @DisplayName("maps CreateMenuItemRequestDto to CreateMenuItemRequest")
        void mapsCreateDtoToRequest() {
            CreateMenuItemRequestDto dto = new CreateMenuItemRequestDto();
            dto.setName("Caesar Salad");
            dto.setDescription("Fresh greens");
            dto.setPriceEur(new BigDecimal("12.50"));
            dto.setCategory("Starters");
            dto.setImageUrl("http://example.com/salad.jpg");

            CreateMenuItemRequest request = menuItemMapper.fromDto(dto);

            assertNotNull(request);
            assertEquals("Caesar Salad", request.getName());
            assertEquals("Fresh greens", request.getDescription());
            assertEquals(0, new BigDecimal("12.50").compareTo(request.getPriceEur()));
            assertEquals("Starters", request.getCategory());
            assertEquals("http://example.com/salad.jpg", request.getImageUrl());
        }

        @Test
        @DisplayName("returns null when dto is null")
        void returnsNullWhenDtoIsNull() {
            assertNull(menuItemMapper.fromDto((CreateMenuItemRequestDto) null));
        }
    }

    @Nested
    @DisplayName("toMenuItemResponseDto")
    class ToMenuItemResponseDto {

        @Test
        @DisplayName("maps MenuItem to MenuItemResponseDto")
        void mapsMenuItemToResponseDto() {
            UUID id = UUID.randomUUID();
            MenuItem item = createMenuItem(id, "Burger", new BigDecimal("15.00"));

            MenuItemResponseDto dto = menuItemMapper.toMenuItemResponseDto(item);

            assertNotNull(dto);
            assertEquals(id, dto.getId());
            assertEquals("Burger", dto.getName());
            assertEquals("Tasty dish", dto.getDescription());
            assertEquals(0, new BigDecimal("15.00").compareTo(dto.getPriceEur()));
            assertEquals("Main", dto.getCategory());
        }

        @Test
        @DisplayName("returns null when entity is null")
        void returnsNullWhenEntityIsNull() {
            assertNull(menuItemMapper.toMenuItemResponseDto(null));
        }
    }

    @Nested
    @DisplayName("fromDto UpdateMenuItemRequestDto")
    class FromDtoUpdate {

        @Test
        @DisplayName("maps UpdateMenuItemRequestDto to UpdateMenuItemRequest")
        void mapsUpdateDtoToRequest() {
            UUID id = UUID.randomUUID();
            UpdateMenuItemRequestDto dto = new UpdateMenuItemRequestDto();
            dto.setId(id);
            dto.setName("Updated Burger");
            dto.setDescription("New description");
            dto.setPriceEur(new BigDecimal("16.00"));
            dto.setCategory("Mains");

            UpdateMenuItemRequest request = menuItemMapper.fromDto(dto);

            assertNotNull(request);
            assertEquals(id, request.getId());
            assertEquals("Updated Burger", request.getName());
            assertEquals(0, new BigDecimal("16.00").compareTo(request.getPriceEur()));
        }

        @Test
        @DisplayName("returns null when dto is null")
        void returnsNullWhenDtoIsNull() {
            assertNull(menuItemMapper.fromDto((UpdateMenuItemRequestDto) null));
        }
    }
}
