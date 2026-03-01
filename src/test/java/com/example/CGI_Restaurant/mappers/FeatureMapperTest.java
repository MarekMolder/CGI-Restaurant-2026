package com.example.CGI_Restaurant.mappers;

/**
 * @author AI (assisted). Used my BookingMapperTest + UserMapperTest.
 */

import com.example.CGI_Restaurant.domain.dtos.createRequests.CreateFeatureRequestDto;
import com.example.CGI_Restaurant.domain.dtos.createResponses.CreateFeatureResponseDto;
import com.example.CGI_Restaurant.domain.dtos.getResponses.GetFeatureDetailsResponseDto;
import com.example.CGI_Restaurant.domain.dtos.listResponses.ListFeatureResponseDto;
import com.example.CGI_Restaurant.domain.dtos.updateRequests.UpdateFeatureRequestDto;
import com.example.CGI_Restaurant.domain.dtos.updateResponses.UpdateFeatureResponseDto;
import com.example.CGI_Restaurant.domain.entities.Feature;
import com.example.CGI_Restaurant.domain.entities.FeatureCodeEnum;
import com.example.CGI_Restaurant.domain.createRequests.CreateFeatureRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateFeatureRequest;
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
class FeatureMapperTest {

    @Autowired
    private FeatureMapper featureMapper;

    private static Feature createFeature(UUID id, FeatureCodeEnum code, String name) {
        LocalDateTime now = LocalDateTime.now();
        return Feature.builder()
                .id(id)
                .code(code)
                .name(name)
                .bookingPreferences(new ArrayList<>())
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    @Nested
    @DisplayName("fromDto CreateFeatureRequestDto")
    class FromDtoCreate {

        @Test
        @DisplayName("maps CreateFeatureRequestDto to CreateFeatureRequest")
        void mapsCreateDtoToRequest() {
            CreateFeatureRequestDto dto = new CreateFeatureRequestDto();
            dto.setCode(FeatureCodeEnum.WINDOW);
            dto.setName("Window view");
            dto.setBookingPreferences(new ArrayList<>());

            CreateFeatureRequest request = featureMapper.fromDto(dto);

            assertNotNull(request);
            assertEquals(FeatureCodeEnum.WINDOW, request.getCode());
            assertEquals("Window view", request.getName());
            assertNotNull(request.getBookingPreferences());
        }

        @Test
        @DisplayName("returns null when dto is null")
        void returnsNullWhenDtoIsNull() {
            assertNull(featureMapper.fromDto((CreateFeatureRequestDto) null));
        }
    }

    @Nested
    @DisplayName("toDto")
    class ToDto {

        @Test
        @DisplayName("maps Feature to CreateFeatureResponseDto")
        void mapsFeatureToCreateResponseDto() {
            UUID id = UUID.randomUUID();
            Feature feature = createFeature(id, FeatureCodeEnum.QUIET, "Quiet area");

            CreateFeatureResponseDto dto = featureMapper.toDto(feature);

            assertNotNull(dto);
            assertEquals(id, dto.getId());
            assertEquals(FeatureCodeEnum.QUIET, dto.getCode());
            assertEquals("Quiet area", dto.getName());
            assertNotNull(dto.getCreatedAt());
            assertNotNull(dto.getUpdatedAt());
        }

        @Test
        @DisplayName("returns null when feature is null")
        void returnsNullWhenFeatureIsNull() {
            assertNull(featureMapper.toDto(null));
        }
    }

    @Nested
    @DisplayName("toListFeatureResponseDto")
    class ToListFeatureResponseDto {

        @Test
        @DisplayName("maps Feature to ListFeatureResponseDto")
        void mapsFeatureToListDto() {
            UUID id = UUID.randomUUID();
            Feature feature = createFeature(id, FeatureCodeEnum.ACCESSIBLE, "Wheelchair accessible");

            ListFeatureResponseDto dto = featureMapper.toListFeatureResponseDto(feature);

            assertNotNull(dto);
            assertEquals(id, dto.getId());
            assertEquals(FeatureCodeEnum.ACCESSIBLE, dto.getCode());
            assertEquals("Wheelchair accessible", dto.getName());
        }

        @Test
        @DisplayName("returns null when feature is null")
        void returnsNullWhenFeatureIsNull() {
            assertNull(featureMapper.toListFeatureResponseDto(null));
        }
    }

    @Nested
    @DisplayName("toGetFeatureDetailsResponseDto")
    class ToGetFeatureDetailsResponseDto {

        @Test
        @DisplayName("maps Feature to GetFeatureDetailsResponseDto")
        void mapsFeatureToGetDetailsDto() {
            UUID id = UUID.randomUUID();
            LocalDateTime now = LocalDateTime.now();
            Feature feature = createFeature(id, FeatureCodeEnum.NEAR_BAR, "Near bar");

            GetFeatureDetailsResponseDto dto = featureMapper.toGetFeatureDetailsResponseDto(feature);

            assertNotNull(dto);
            assertEquals(id, dto.getId());
            assertEquals(FeatureCodeEnum.NEAR_BAR, dto.getCode());
            assertEquals("Near bar", dto.getName());
            assertNotNull(dto.getCreatedAt());
            assertNotNull(dto.getUpdatedAt());
        }

        @Test
        @DisplayName("returns null when feature is null")
        void returnsNullWhenFeatureIsNull() {
            assertNull(featureMapper.toGetFeatureDetailsResponseDto(null));
        }
    }

    @Nested
    @DisplayName("fromDto UpdateFeatureRequestDto")
    class FromDtoUpdate {

        @Test
        @DisplayName("maps UpdateFeatureRequestDto to UpdateFeatureRequest")
        void mapsUpdateDtoToRequest() {
            UUID id = UUID.randomUUID();
            UpdateFeatureRequestDto dto = new UpdateFeatureRequestDto();
            dto.setId(id);
            dto.setCode(FeatureCodeEnum.PRIVACY);
            dto.setName("Private booth");
            dto.setBookingPreferences(new ArrayList<>());

            UpdateFeatureRequest request = featureMapper.fromDto(dto);

            assertNotNull(request);
            assertEquals(id, request.getId());
            assertEquals(FeatureCodeEnum.PRIVACY, request.getCode());
            assertEquals("Private booth", request.getName());
        }

        @Test
        @DisplayName("returns null when dto is null")
        void returnsNullWhenDtoIsNull() {
            assertNull(featureMapper.fromDto((UpdateFeatureRequestDto) null));
        }
    }

    @Nested
    @DisplayName("toUpdateFeatureResponseDto")
    class ToUpdateFeatureResponseDto {

        @Test
        @DisplayName("maps Feature to UpdateFeatureResponseDto")
        void mapsFeatureToUpdateResponseDto() {
            UUID id = UUID.randomUUID();
            Feature feature = createFeature(id, FeatureCodeEnum.CENTER, "Center table");

            UpdateFeatureResponseDto dto = featureMapper.toUpdateFeatureResponseDto(feature);

            assertNotNull(dto);
            assertEquals(id, dto.getId());
            assertEquals(FeatureCodeEnum.CENTER, dto.getCode());
            assertEquals("Center table", dto.getName());
        }

        @Test
        @DisplayName("returns null when feature is null")
        void returnsNullWhenFeatureIsNull() {
            assertNull(featureMapper.toUpdateFeatureResponseDto(null));
        }
    }
}
