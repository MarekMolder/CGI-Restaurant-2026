package com.example.CGI_Restaurant.mappers;

/**
 * @author AI (assisted). Used my BookingMapperTest + UserMapperTest.
 */

import com.example.CGI_Restaurant.domain.dtos.createRequests.CreateTableEntityRequestDto;
import com.example.CGI_Restaurant.domain.dtos.createResponses.CreateTableEntityResponseDto;
import com.example.CGI_Restaurant.domain.dtos.getResponses.GetTableEntityDetailsResponseDto;
import com.example.CGI_Restaurant.domain.dtos.listResponses.ListTableEntityResponseDto;
import com.example.CGI_Restaurant.domain.dtos.listResponses.TableAvailabilityItemDto;
import com.example.CGI_Restaurant.domain.dtos.updateRequests.UpdateTableEntityRequestDto;
import com.example.CGI_Restaurant.domain.dtos.updateResponses.UpdateTableEntityResponseDto;
import com.example.CGI_Restaurant.domain.entities.TableEntity;
import com.example.CGI_Restaurant.domain.entities.TableShapeEnum;
import com.example.CGI_Restaurant.domain.entities.Zone;
import com.example.CGI_Restaurant.domain.entities.ZoneTypeEnum;
import com.example.CGI_Restaurant.domain.createRequests.CreateTableEntityRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateTableEntityRequest;
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
class TableEntityMapperTest {

    @Autowired
    private TableEntityMapper tableEntityMapper;

    private static TableEntity createTableEntity(UUID id, String label, Zone zone) {
        LocalDateTime now = LocalDateTime.now();
        return TableEntity.builder()
                .id(id)
                .label(label)
                .capacity(4)
                .minPartySize(2)
                .shape(TableShapeEnum.RECT)
                .x(10.0)
                .y(20.0)
                .width(80.0)
                .height(120.0)
                .rotationDegree(0)
                .active(true)
                .zone(zone)
                .bookingTables(new ArrayList<>())
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    private static Zone createZone(UUID zoneId, String name, ZoneTypeEnum type) {
        Zone z = new Zone();
        z.setId(zoneId);
        z.setName(name);
        z.setType(type);
        z.setColor("#cccccc");
        return z;
    }

    @Nested
    @DisplayName("fromDto CreateTableEntityRequestDto")
    class FromDtoCreate {

        @Test
        @DisplayName("maps CreateTableEntityRequestDto to CreateTableEntityRequest")
        void mapsCreateDtoToRequest() {
            CreateTableEntityRequestDto dto = new CreateTableEntityRequestDto();
            dto.setLabel("T1");
            dto.setCapacity(4);
            dto.setMinPartySize(2);
            dto.setShape(TableShapeEnum.CIRCLE);
            dto.setX(5.0);
            dto.setY(10.0);
            dto.setWidth(100.0);
            dto.setHeight(100.0);
            dto.setRotationDegree(45);
            dto.setActive(true);
            dto.setAdjacentTableIds(new ArrayList<>());

            CreateTableEntityRequest request = tableEntityMapper.fromDto(dto);

            assertNotNull(request);
            assertEquals("T1", request.getLabel());
            assertEquals(4, request.getCapacity());
            assertEquals(TableShapeEnum.CIRCLE, request.getShape());
            assertEquals(45, request.getRotationDegree());
        }

        @Test
        @DisplayName("returns null when dto is null")
        void returnsNullWhenDtoIsNull() {
            assertNull(tableEntityMapper.fromDto((CreateTableEntityRequestDto) null));
        }
    }

    @Nested
    @DisplayName("toDto")
    class ToDto {

        @Test
        @DisplayName("maps TableEntity to CreateTableEntityResponseDto")
        void mapsToCreateResponseDto() {
            UUID id = UUID.randomUUID();
            TableEntity table = createTableEntity(id, "Table 5", null);

            CreateTableEntityResponseDto dto = tableEntityMapper.toDto(table);

            assertNotNull(dto);
            assertEquals(id, dto.getId());
            assertEquals("Table 5", dto.getLabel());
            assertEquals(4, dto.getCapacity());
            assertEquals(TableShapeEnum.RECT, dto.getShape());
        }

        @Test
        @DisplayName("returns null when entity is null")
        void returnsNullWhenEntityIsNull() {
            assertNull(tableEntityMapper.toDto(null));
        }
    }

    @Nested
    @DisplayName("toListTableEntityResponseDto")
    class ToListTableEntityResponseDto {

        @Test
        @DisplayName("maps TableEntity to ListTableEntityResponseDto")
        void mapsToListDto() {
            UUID id = UUID.randomUUID();
            TableEntity table = createTableEntity(id, "T2", null);

            ListTableEntityResponseDto dto = tableEntityMapper.toListTableEntityResponseDto(table);

            assertNotNull(dto);
            assertEquals(id, dto.getId());
            assertEquals("T2", dto.getLabel());
        }

        @Test
        @DisplayName("returns null when entity is null")
        void returnsNullWhenEntityIsNull() {
            assertNull(tableEntityMapper.toListTableEntityResponseDto(null));
        }
    }

    @Nested
    @DisplayName("toGetTableEntityDetailsResponseDto")
    class ToGetTableEntityDetailsResponseDto {

        @Test
        @DisplayName("maps TableEntity to GetTableEntityDetailsResponseDto")
        void mapsToGetDetailsDto() {
            UUID id = UUID.randomUUID();
            TableEntity table = createTableEntity(id, "T3", null);

            GetTableEntityDetailsResponseDto dto = tableEntityMapper.toGetTableEntityDetailsResponseDto(table);

            assertNotNull(dto);
            assertEquals(id, dto.getId());
            assertEquals("T3", dto.getLabel());
        }

        @Test
        @DisplayName("returns null when entity is null")
        void returnsNullWhenEntityIsNull() {
            assertNull(tableEntityMapper.toGetTableEntityDetailsResponseDto(null));
        }
    }

    @Nested
    @DisplayName("fromDto UpdateTableEntityRequestDto")
    class FromDtoUpdate {

        @Test
        @DisplayName("maps UpdateTableEntityRequestDto to UpdateTableEntityRequest")
        void mapsUpdateDtoToRequest() {
            UUID id = UUID.randomUUID();
            UpdateTableEntityRequestDto dto = new UpdateTableEntityRequestDto();
            dto.setId(id);
            dto.setLabel("Updated");
            dto.setCapacity(6);
            dto.setMinPartySize(2);
            dto.setShape(TableShapeEnum.OVAL);
            dto.setX(0.0);
            dto.setY(0.0);
            dto.setWidth(120.0);
            dto.setHeight(80.0);
            dto.setRotationDegree(90);
            dto.setActive(false);

            UpdateTableEntityRequest request = tableEntityMapper.fromDto(dto);

            assertNotNull(request);
            assertEquals(id, request.getId());
            assertEquals("Updated", request.getLabel());
            assertEquals(6, request.getCapacity());
        }

        @Test
        @DisplayName("returns null when dto is null")
        void returnsNullWhenDtoIsNull() {
            assertNull(tableEntityMapper.fromDto((UpdateTableEntityRequestDto) null));
        }
    }

    @Nested
    @DisplayName("toUpdateTableEntityResponseDto")
    class ToUpdateTableEntityResponseDto {

        @Test
        @DisplayName("maps TableEntity to UpdateTableEntityResponseDto")
        void mapsToUpdateResponseDto() {
            UUID id = UUID.randomUUID();
            TableEntity table = createTableEntity(id, "T4", null);

            UpdateTableEntityResponseDto dto = tableEntityMapper.toUpdateTableEntityResponseDto(table);

            assertNotNull(dto);
            assertEquals(id, dto.getId());
            assertEquals("T4", dto.getLabel());
        }

        @Test
        @DisplayName("returns null when entity is null")
        void returnsNullWhenEntityIsNull() {
            assertNull(tableEntityMapper.toUpdateTableEntityResponseDto(null));
        }
    }

    @Nested
    @DisplayName("toTableAvailabilityItemDto")
    class ToTableAvailabilityItemDto {

        @Test
        @DisplayName("maps TableEntity with zone to TableAvailabilityItemDto")
        void mapsTableWithZoneToAvailabilityDto() {
            UUID tableId = UUID.randomUUID();
            UUID zoneId = UUID.randomUUID();
            Zone zone = createZone(zoneId, "Terrace", ZoneTypeEnum.TERRACE);
            TableEntity table = createTableEntity(tableId, "T10", zone);

            TableAvailabilityItemDto dto = tableEntityMapper.toTableAvailabilityItemDto(table, true, 85);

            assertNotNull(dto);
            assertEquals(tableId, dto.getId());
            assertNotNull(dto.getTableIds());
            assertEquals(1, dto.getTableIds().size());
            assertEquals(tableId, dto.getTableIds().get(0));
            assertFalse(dto.isCombined());
            assertEquals("T10", dto.getLabel());
            assertEquals(4, dto.getCapacity());
            assertEquals(2, dto.getMinPartySize());
            assertEquals(TableShapeEnum.RECT, dto.getShape());
            assertEquals(zoneId, dto.getZoneId());
            assertEquals("Terrace", dto.getZoneName());
            assertEquals(ZoneTypeEnum.TERRACE, dto.getZoneType());
            assertTrue(dto.isAvailable());
            assertEquals(85, dto.getRecommendationScore());
        }

        @Test
        @DisplayName("maps TableEntity with available false and null recommendationScore")
        void mapsWithAvailableFalseAndNullScore() {
            UUID tableId = UUID.randomUUID();
            Zone zone = createZone(UUID.randomUUID(), "Indoor", ZoneTypeEnum.INDOOR);
            TableEntity table = createTableEntity(tableId, "T20", zone);

            TableAvailabilityItemDto dto = tableEntityMapper.toTableAvailabilityItemDto(table, false, null);

            assertNotNull(dto);
            assertFalse(dto.isAvailable());
            assertNull(dto.getRecommendationScore());
        }
    }
}
