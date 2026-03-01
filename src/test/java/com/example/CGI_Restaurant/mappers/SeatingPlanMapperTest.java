package com.example.CGI_Restaurant.mappers;

/**
 * @author AI (assisted). Used my BookingMapperTest + UserMapperTest.
 */

import com.example.CGI_Restaurant.domain.dtos.createRequests.CreateSeatingPlanRequestDto;
import com.example.CGI_Restaurant.domain.dtos.createResponses.CreateSeatingPlanResponseDto;
import com.example.CGI_Restaurant.domain.dtos.getResponses.GetSeatingPlanDetailsResponseDto;
import com.example.CGI_Restaurant.domain.dtos.listResponses.ListSeatingPlanResponseDto;
import com.example.CGI_Restaurant.domain.dtos.updateRequests.UpdateSeatingPlanRequestDto;
import com.example.CGI_Restaurant.domain.dtos.updateResponses.UpdateSeatingPlanResponseDto;
import com.example.CGI_Restaurant.domain.entities.SeatingPlan;
import com.example.CGI_Restaurant.domain.entities.SeatingPlanTypeEnum;
import com.example.CGI_Restaurant.domain.createRequests.CreateSeatingPlanRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateSeatingPlanRequest;
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
class SeatingPlanMapperTest {

    @Autowired
    private SeatingPlanMapper seatingPlanMapper;

    private static SeatingPlan createSeatingPlan(UUID id, String name, SeatingPlanTypeEnum type) {
        LocalDateTime now = LocalDateTime.now();
        return SeatingPlan.builder()
                .id(id)
                .name(name)
                .type(type)
                .width(800.0)
                .height(600.0)
                .backgroundSVG(null)
                .active(true)
                .version(1)
                .tableEntities(new ArrayList<>())
                .zones(new ArrayList<>())
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    @Nested
    @DisplayName("fromDto CreateSeatingPlanRequestDto")
    class FromDtoCreate {

        @Test
        @DisplayName("maps CreateSeatingPlanRequestDto to CreateSeatingPlanRequest")
        void mapsCreateDtoToRequest() {
            CreateSeatingPlanRequestDto dto = new CreateSeatingPlanRequestDto();
            dto.setName("Main floor");
            dto.setType(SeatingPlanTypeEnum.OUTDOOR);
            dto.setWidth(1000.0);
            dto.setHeight(800.0);
            dto.setBackgroundSVG("<svg/>");
            dto.setActive(true);
            dto.setVersion(0);
            dto.setTableEntities(new ArrayList<>());
            dto.setZones(new ArrayList<>());

            CreateSeatingPlanRequest request = seatingPlanMapper.fromDto(dto);

            assertNotNull(request);
            assertEquals("Main floor", request.getName());
            assertEquals(SeatingPlanTypeEnum.OUTDOOR, request.getType());
            assertEquals(1000.0, request.getWidth(), 0.001);
            assertEquals(800.0, request.getHeight(), 0.001);
            assertEquals(0, request.getVersion());
        }

        @Test
        @DisplayName("returns null when dto is null")
        void returnsNullWhenDtoIsNull() {
            assertNull(seatingPlanMapper.fromDto((CreateSeatingPlanRequestDto) null));
        }
    }

    @Nested
    @DisplayName("toDto")
    class ToDto {

        @Test
        @DisplayName("maps SeatingPlan to CreateSeatingPlanResponseDto")
        void mapsToCreateResponseDto() {
            UUID id = UUID.randomUUID();
            SeatingPlan sp = createSeatingPlan(id, "Terrace plan", SeatingPlanTypeEnum.OUTDOOR);

            CreateSeatingPlanResponseDto dto = seatingPlanMapper.toDto(sp);

            assertNotNull(dto);
            assertEquals(id, dto.getId());
            assertEquals("Terrace plan", dto.getName());
            assertEquals(SeatingPlanTypeEnum.OUTDOOR, dto.getType());
        }

        @Test
        @DisplayName("returns null when entity is null")
        void returnsNullWhenEntityIsNull() {
            assertNull(seatingPlanMapper.toDto(null));
        }
    }

    @Nested
    @DisplayName("toListSeatingPlanResponseDto")
    class ToListSeatingPlanResponseDto {

        @Test
        @DisplayName("maps SeatingPlan to ListSeatingPlanResponseDto")
        void mapsToListDto() {
            UUID id = UUID.randomUUID();
            SeatingPlan sp = createSeatingPlan(id, "Floor 1", SeatingPlanTypeEnum.FLOOR_1);

            ListSeatingPlanResponseDto dto = seatingPlanMapper.toListSeatingPlanResponseDto(sp);

            assertNotNull(dto);
            assertEquals(id, dto.getId());
            assertEquals("Floor 1", dto.getName());
        }

        @Test
        @DisplayName("returns null when entity is null")
        void returnsNullWhenEntityIsNull() {
            assertNull(seatingPlanMapper.toListSeatingPlanResponseDto(null));
        }
    }

    @Nested
    @DisplayName("toGetSeatingPlanDetailsResponseDto")
    class ToGetSeatingPlanDetailsResponseDto {

        @Test
        @DisplayName("maps SeatingPlan to GetSeatingPlanDetailsResponseDto")
        void mapsToGetDetailsDto() {
            UUID id = UUID.randomUUID();
            SeatingPlan sp = createSeatingPlan(id, "Private area", SeatingPlanTypeEnum.PRIVATE_AREA);

            GetSeatingPlanDetailsResponseDto dto = seatingPlanMapper.toGetSeatingPlanDetailsResponseDto(sp);

            assertNotNull(dto);
            assertEquals(id, dto.getId());
            assertEquals("Private area", dto.getName());
            assertNotNull(dto.getCreatedAt());
        }

        @Test
        @DisplayName("returns null when entity is null")
        void returnsNullWhenEntityIsNull() {
            assertNull(seatingPlanMapper.toGetSeatingPlanDetailsResponseDto(null));
        }
    }

    @Nested
    @DisplayName("fromDto UpdateSeatingPlanRequestDto")
    class FromDtoUpdate {

        @Test
        @DisplayName("maps UpdateSeatingPlanRequestDto to UpdateSeatingPlanRequest")
        void mapsUpdateDtoToRequest() {
            UUID id = UUID.randomUUID();
            UpdateSeatingPlanRequestDto dto = new UpdateSeatingPlanRequestDto();
            dto.setId(id);
            dto.setName("Updated plan");
            dto.setType(SeatingPlanTypeEnum.FLOOR_2);
            dto.setWidth(1200.0);
            dto.setHeight(900.0);
            dto.setActive(false);
            dto.setVersion(2);
            dto.setTableEntities(new ArrayList<>());
            dto.setZones(new ArrayList<>());

            UpdateSeatingPlanRequest request = seatingPlanMapper.fromDto(dto);

            assertNotNull(request);
            assertEquals(id, request.getId());
            assertEquals("Updated plan", request.getName());
            assertEquals(2, request.getVersion());
        }

        @Test
        @DisplayName("returns null when dto is null")
        void returnsNullWhenDtoIsNull() {
            assertNull(seatingPlanMapper.fromDto((UpdateSeatingPlanRequestDto) null));
        }
    }

    @Nested
    @DisplayName("toUpdateSeatingPlanResponseDto")
    class ToUpdateSeatingPlanResponseDto {

        @Test
        @DisplayName("maps SeatingPlan to UpdateSeatingPlanResponseDto")
        void mapsToUpdateResponseDto() {
            UUID id = UUID.randomUUID();
            SeatingPlan sp = createSeatingPlan(id, "Updated", SeatingPlanTypeEnum.OUTDOOR);

            UpdateSeatingPlanResponseDto dto = seatingPlanMapper.toUpdateSeatingPlanResponseDto(sp);

            assertNotNull(dto);
            assertEquals(id, dto.getId());
            assertEquals("Updated", dto.getName());
        }

        @Test
        @DisplayName("returns null when entity is null")
        void returnsNullWhenEntityIsNull() {
            assertNull(seatingPlanMapper.toUpdateSeatingPlanResponseDto(null));
        }
    }
}
