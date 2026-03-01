package com.example.CGI_Restaurant.mappers;

/**
 * @author AI (assisted). Used my BookingMapperTest + UserMapperTest.
 */

import com.example.CGI_Restaurant.domain.dtos.createRequests.CreateRestaurantRequestDto;
import com.example.CGI_Restaurant.domain.dtos.createResponses.CreateRestaurantResponseDto;
import com.example.CGI_Restaurant.domain.dtos.getResponses.GetRestaurantDetailsResponseDto;
import com.example.CGI_Restaurant.domain.dtos.listResponses.ListRestaurantResponseDto;
import com.example.CGI_Restaurant.domain.dtos.updateRequests.UpdateRestaurantRequestDto;
import com.example.CGI_Restaurant.domain.dtos.updateResponses.UpdateRestaurantResponseDto;
import com.example.CGI_Restaurant.domain.entities.Restaurant;
import com.example.CGI_Restaurant.domain.createRequests.CreateRestaurantRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateRestaurantRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Import(com.example.CGI_Restaurant.TestMailConfig.class)
class RestaurantMapperTest {

    @Autowired
    private RestaurantMapper restaurantMapper;

    private static Restaurant createRestaurant(UUID id, String name) {
        LocalDateTime now = LocalDateTime.now();
        return Restaurant.builder()
                .id(id)
                .name(name)
                .timezone("Europe/Tallinn")
                .email("restaurant@example.com")
                .phone("+372 6000000")
                .address("Main Street 1")
                .seatingPlans(new ArrayList<>())
                .menuItems(new ArrayList<>())
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    @Nested
    @DisplayName("fromDto CreateRestaurantRequestDto")
    class FromDtoCreate {

        @Test
        @DisplayName("maps CreateRestaurantRequestDto to CreateRestaurantRequest")
        void mapsCreateDtoToRequest() {
            CreateRestaurantRequestDto dto = new CreateRestaurantRequestDto();
            dto.setName("Test Restaurant");
            dto.setTimezone("Europe/Tallinn");
            dto.setEmail("info@test.com");
            dto.setPhone("+372 1234567");
            dto.setAddress("Test Ave 1");
            dto.setSeatingPlans(new ArrayList<>());

            CreateRestaurantRequest request = restaurantMapper.fromDto(dto);

            assertNotNull(request);
            assertEquals("Test Restaurant", request.getName());
            assertEquals("Europe/Tallinn", request.getTimezone());
            assertEquals("info@test.com", request.getEmail());
        }

        @Test
        @DisplayName("returns null when dto is null")
        void returnsNullWhenDtoIsNull() {
            assertNull(restaurantMapper.fromDto((CreateRestaurantRequestDto) null));
        }
    }

    @Nested
    @DisplayName("toDto")
    class ToDto {

        @Test
        @DisplayName("maps Restaurant to CreateRestaurantResponseDto")
        void mapsToCreateResponseDto() {
            UUID id = UUID.randomUUID();
            Restaurant restaurant = createRestaurant(id, "CGI Restaurant");

            CreateRestaurantResponseDto dto = restaurantMapper.toDto(restaurant);

            assertNotNull(dto);
            assertEquals(id, dto.getId());
            assertEquals("CGI Restaurant", dto.getName());
            assertEquals("Europe/Tallinn", dto.getTimezone());
        }

        @Test
        @DisplayName("returns null when entity is null")
        void returnsNullWhenEntityIsNull() {
            assertNull(restaurantMapper.toDto(null));
        }
    }

    @Nested
    @DisplayName("toListRestaurantResponseDto")
    class ToListRestaurantResponseDto {

        @Test
        @DisplayName("maps Restaurant to ListRestaurantResponseDto")
        void mapsToListDto() {
            UUID id = UUID.randomUUID();
            Restaurant restaurant = createRestaurant(id, "Listed Restaurant");

            ListRestaurantResponseDto dto = restaurantMapper.toListRestaurantResponseDto(restaurant);

            assertNotNull(dto);
            assertEquals(id, dto.getId());
            assertEquals("Listed Restaurant", dto.getName());
        }

        @Test
        @DisplayName("returns null when entity is null")
        void returnsNullWhenEntityIsNull() {
            assertNull(restaurantMapper.toListRestaurantResponseDto(null));
        }
    }

    @Nested
    @DisplayName("toGetRestaurantDetailsResponseDto")
    class ToGetRestaurantDetailsResponseDto {

        @Test
        @DisplayName("maps Restaurant to GetRestaurantDetailsResponseDto")
        void mapsToGetDetailsDto() {
            UUID id = UUID.randomUUID();
            Restaurant restaurant = createRestaurant(id, "Details Restaurant");

            GetRestaurantDetailsResponseDto dto = restaurantMapper.toGetRestaurantDetailsResponseDto(restaurant);

            assertNotNull(dto);
            assertEquals(id, dto.getId());
            assertEquals("Details Restaurant", dto.getName());
            assertNotNull(dto.getCreatedAt());
        }

        @Test
        @DisplayName("returns null when entity is null")
        void returnsNullWhenEntityIsNull() {
            assertNull(restaurantMapper.toGetRestaurantDetailsResponseDto(null));
        }
    }

    @Nested
    @DisplayName("fromDto UpdateRestaurantRequestDto")
    class FromDtoUpdate {

        @Test
        @DisplayName("maps UpdateRestaurantRequestDto to UpdateRestaurantRequest")
        void mapsUpdateDtoToRequest() {
            UUID id = UUID.randomUUID();
            UpdateRestaurantRequestDto dto = new UpdateRestaurantRequestDto();
            dto.setId(id);
            dto.setName("Updated Restaurant");
            dto.setTimezone("UTC");
            dto.setEmail("updated@example.com");
            dto.setPhone("+372 9999999");
            dto.setAddress("New Address 2");
            dto.setSeatingPlans(new ArrayList<>());

            UpdateRestaurantRequest request = restaurantMapper.fromDto(dto);

            assertNotNull(request);
            assertEquals(id, request.getId());
            assertEquals("Updated Restaurant", request.getName());
        }

        @Test
        @DisplayName("returns null when dto is null")
        void returnsNullWhenDtoIsNull() {
            assertNull(restaurantMapper.fromDto((UpdateRestaurantRequestDto) null));
        }
    }

    @Nested
    @DisplayName("toUpdateRestaurantResponseDto")
    class ToUpdateRestaurantResponseDto {

        @Test
        @DisplayName("maps Restaurant to UpdateRestaurantResponseDto")
        void mapsToUpdateResponseDto() {
            UUID id = UUID.randomUUID();
            Restaurant restaurant = createRestaurant(id, "Updated Name");

            UpdateRestaurantResponseDto dto = restaurantMapper.toUpdateRestaurantResponseDto(restaurant);

            assertNotNull(dto);
            assertEquals(id, dto.getId());
            assertEquals("Updated Name", dto.getName());
        }

        @Test
        @DisplayName("returns null when entity is null")
        void returnsNullWhenEntityIsNull() {
            assertNull(restaurantMapper.toUpdateRestaurantResponseDto(null));
        }
    }
}
