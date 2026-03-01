package com.example.CGI_Restaurant.mappers;

/**
 * @author AI (assisted). Used my BookingMapperTest + UserMapperTest.
 */

import com.example.CGI_Restaurant.domain.dtos.createRequests.CreateZoneRequestDto;
import com.example.CGI_Restaurant.domain.dtos.createResponses.CreateZoneResponseDto;
import com.example.CGI_Restaurant.domain.dtos.getResponses.GetZoneDetailsResponseDto;
import com.example.CGI_Restaurant.domain.dtos.listResponses.ListZoneResponseDto;
import com.example.CGI_Restaurant.domain.dtos.updateRequests.UpdateZoneRequestDto;
import com.example.CGI_Restaurant.domain.dtos.updateResponses.UpdateZoneResponseDto;
import com.example.CGI_Restaurant.domain.entities.Zone;
import com.example.CGI_Restaurant.domain.entities.ZoneTypeEnum;
import com.example.CGI_Restaurant.domain.createRequests.CreateZoneRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateZoneRequest;
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
class ZoneMapperTest {

    @Autowired
    private ZoneMapper zoneMapper;

    private static Zone createZone(UUID id, String name, ZoneTypeEnum type) {
        LocalDateTime now = LocalDateTime.now();
        Zone z = new Zone();
        z.setId(id);
        z.setName(name);
        z.setType(type);
        z.setColor("#ff0000");
        z.setTableEntities(new ArrayList<>());
        z.setCreatedAt(now);
        z.setUpdatedAt(now);
        return z;
    }

    @Nested
    @DisplayName("fromDto CreateZoneRequestDto")
    class FromDtoCreate {

        @Test
        @DisplayName("maps CreateZoneRequestDto to CreateZoneRequest")
        void mapsCreateDtoToRequest() {
            CreateZoneRequestDto dto = new CreateZoneRequestDto();
            dto.setName("Terrace");
            dto.setType(ZoneTypeEnum.TERRACE);
            dto.setColor("#00ff00");
            dto.setTableEntities(new ArrayList<>());

            CreateZoneRequest request = zoneMapper.fromDto(dto);

            assertNotNull(request);
            assertEquals("Terrace", request.getName());
            assertEquals(ZoneTypeEnum.TERRACE, request.getType());
            assertEquals("#00ff00", request.getColor());
        }

        @Test
        @DisplayName("returns null when dto is null")
        void returnsNullWhenDtoIsNull() {
            assertNull(zoneMapper.fromDto((CreateZoneRequestDto) null));
        }
    }

    @Nested
    @DisplayName("toDto")
    class ToDto {

        @Test
        @DisplayName("maps Zone to CreateZoneResponseDto")
        void mapsToCreateResponseDto() {
            UUID id = UUID.randomUUID();
            Zone zone = createZone(id, "Bar area", ZoneTypeEnum.BAR);

            CreateZoneResponseDto dto = zoneMapper.toDto(zone);

            assertNotNull(dto);
            assertEquals(id, dto.getId());
            assertEquals("Bar area", dto.getName());
            assertEquals(ZoneTypeEnum.BAR, dto.getType());
        }

        @Test
        @DisplayName("returns null when entity is null")
        void returnsNullWhenEntityIsNull() {
            assertNull(zoneMapper.toDto(null));
        }
    }

    @Nested
    @DisplayName("toListZoneResponseDto")
    class ToListZoneResponseDto {

        @Test
        @DisplayName("maps Zone to ListZoneResponseDto")
        void mapsToListDto() {
            UUID id = UUID.randomUUID();
            Zone zone = createZone(id, "Kids zone", ZoneTypeEnum.KIDS);

            ListZoneResponseDto dto = zoneMapper.toListZoneResponseDto(zone);

            assertNotNull(dto);
            assertEquals(id, dto.getId());
            assertEquals("Kids zone", dto.getName());
        }

        @Test
        @DisplayName("returns null when entity is null")
        void returnsNullWhenEntityIsNull() {
            assertNull(zoneMapper.toListZoneResponseDto(null));
        }
    }

    @Nested
    @DisplayName("toGetZoneDetailsResponseDto")
    class ToGetZoneDetailsResponseDto {

        @Test
        @DisplayName("maps Zone to GetZoneDetailsResponseDto")
        void mapsToGetDetailsDto() {
            UUID id = UUID.randomUUID();
            Zone zone = createZone(id, "Private room", ZoneTypeEnum.PRIVATE);

            GetZoneDetailsResponseDto dto = zoneMapper.toGetZoneDetailsResponseDto(zone);

            assertNotNull(dto);
            assertEquals(id, dto.getId());
            assertEquals("Private room", dto.getName());
            assertEquals(ZoneTypeEnum.PRIVATE, dto.getType());
        }

        @Test
        @DisplayName("returns null when entity is null")
        void returnsNullWhenEntityIsNull() {
            assertNull(zoneMapper.toGetZoneDetailsResponseDto(null));
        }
    }

    @Nested
    @DisplayName("fromDto UpdateZoneRequestDto")
    class FromDtoUpdate {

        @Test
        @DisplayName("maps UpdateZoneRequestDto to UpdateZoneRequest")
        void mapsUpdateDtoToRequest() {
            UUID id = UUID.randomUUID();
            UpdateZoneRequestDto dto = new UpdateZoneRequestDto();
            dto.setId(id);
            dto.setName("Updated zone");
            dto.setType(ZoneTypeEnum.INDOOR);
            dto.setColor("#0000ff");

            UpdateZoneRequest request = zoneMapper.fromDto(dto);

            assertNotNull(request);
            assertEquals(id, request.getId());
            assertEquals("Updated zone", request.getName());
            assertEquals(ZoneTypeEnum.INDOOR, request.getType());
        }

        @Test
        @DisplayName("returns null when dto is null")
        void returnsNullWhenDtoIsNull() {
            assertNull(zoneMapper.fromDto((UpdateZoneRequestDto) null));
        }
    }

    @Nested
    @DisplayName("toUpdateZoneResponseDto")
    class ToUpdateZoneResponseDto {

        @Test
        @DisplayName("maps Zone to UpdateZoneResponseDto")
        void mapsToUpdateResponseDto() {
            UUID id = UUID.randomUUID();
            Zone zone = createZone(id, "Other", ZoneTypeEnum.OTHER);

            UpdateZoneResponseDto dto = zoneMapper.toUpdateZoneResponseDto(zone);

            assertNotNull(dto);
            assertEquals(id, dto.getId());
            assertEquals("Other", dto.getName());
        }

        @Test
        @DisplayName("returns null when entity is null")
        void returnsNullWhenEntityIsNull() {
            assertNull(zoneMapper.toUpdateZoneResponseDto(null));
        }
    }
}
