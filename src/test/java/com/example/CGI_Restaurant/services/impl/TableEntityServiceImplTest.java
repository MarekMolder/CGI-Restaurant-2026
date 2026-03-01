package com.example.CGI_Restaurant.services.impl;

/**
 * @author AI (assisted). Used my BookingSeviceImplTest.
 */

import com.example.CGI_Restaurant.domain.dtos.listResponses.TableAvailabilityItemDto;
import com.example.CGI_Restaurant.domain.entities.TableEntity;
import com.example.CGI_Restaurant.domain.entities.TableShapeEnum;
import com.example.CGI_Restaurant.domain.entities.Zone;
import com.example.CGI_Restaurant.domain.entities.ZoneTypeEnum;
import com.example.CGI_Restaurant.domain.createRequests.CreateTableEntityRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateTableEntityRequest;
import com.example.CGI_Restaurant.exceptions.RestaurantBookingException;
import com.example.CGI_Restaurant.exceptions.notFoundExceptions.TableEntityNotFoundException;
import com.example.CGI_Restaurant.exceptions.updateException.TableEntityUpdateException;
import com.example.CGI_Restaurant.mappers.TableEntityMapper;
import com.example.CGI_Restaurant.repositories.BookingTableRepository;
import com.example.CGI_Restaurant.repositories.TableEntityRepository;
import com.example.CGI_Restaurant.repositories.ZoneRepository;
import com.example.CGI_Restaurant.services.RestaurantHoursService;
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

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TableEntityServiceImplTest {

    @Mock
    private TableEntityRepository tableEntityRepository;

    @Mock
    private BookingTableRepository bookingTableRepository;

    @Mock
    private ZoneRepository zoneRepository;

    @Mock
    private TableEntityMapper tableEntityMapper;

    @Mock
    private RestaurantHoursService restaurantHoursService;

    @InjectMocks
    private TableEntityServiceImpl service;

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("saves new table with request data and no adjacent")
        void createsTableWithNoAdjacent() {
            CreateTableEntityRequest request = new CreateTableEntityRequest();
            request.setLabel("Laud 1");
            request.setCapacity(4);
            request.setMinPartySize(2);
            request.setShape(TableShapeEnum.RECT);
            request.setX(10);
            request.setY(20);
            request.setWidth(80);
            request.setHeight(120);
            request.setRotationDegree(0);
            request.setActive(true);
            request.setAdjacentTableIds(List.of());

            UUID id = UUID.randomUUID();
            TableEntity saved = TableEntity.builder().id(id).label("Laud 1").capacity(4).minPartySize(2)
                    .shape(TableShapeEnum.RECT).x(10).y(20).width(80).height(120).rotationDegree(0).active(true)
                    .adjacentTables(new HashSet<>()).build();
            when(tableEntityRepository.save(any(TableEntity.class))).thenReturn(saved);

            TableEntity result = service.create(request);

            assertNotNull(result);
            assertEquals("Laud 1", result.getLabel());
            assertEquals(4, result.getCapacity());
            verify(tableEntityRepository, times(1)).save(any(TableEntity.class));
        }

        @Test
        @DisplayName("saves and syncs adjacent tables when adjacent ids provided")
        void createsAndSyncsAdjacent() {
            UUID id1 = UUID.randomUUID();
            UUID id2 = UUID.randomUUID();
            CreateTableEntityRequest request = new CreateTableEntityRequest();
            request.setLabel("Laud A");
            request.setCapacity(4);
            request.setMinPartySize(1);
            request.setShape(TableShapeEnum.CIRCLE);
            request.setX(0);
            request.setY(0);
            request.setWidth(60);
            request.setHeight(60);
            request.setRotationDegree(0);
            request.setActive(true);
            request.setAdjacentTableIds(List.of(id2));

            TableEntity table1 = TableEntity.builder().id(id1).label("Laud A").capacity(4).minPartySize(1)
                    .shape(TableShapeEnum.CIRCLE).adjacentTables(new HashSet<>()).build();
            TableEntity table2 = TableEntity.builder().id(id2).label("Laud B").capacity(2).minPartySize(1)
                    .shape(TableShapeEnum.RECT).adjacentTables(new HashSet<>()).build();

            when(tableEntityRepository.save(any(TableEntity.class))).thenAnswer(inv -> {
                TableEntity e = inv.getArgument(0);
                if (e.getId() == null) e.setId(id1);
                if (e.getAdjacentTables() == null) e.setAdjacentTables(new HashSet<>());
                return e;
            });
            when(tableEntityRepository.findById(id2)).thenReturn(Optional.of(table2));

            TableEntity result = service.create(request);

            assertNotNull(result);
            verify(tableEntityRepository).findById(id2);
            verify(tableEntityRepository, atLeast(2)).save(any(TableEntity.class));
        }
    }

    @Nested
    @DisplayName("list")
    class ListMethod {

        @Test
        @DisplayName("returns page from repository")
        void returnsPage() {
            Pageable pageable = PageRequest.of(0, 10);
            TableEntity t = TableEntity.builder().id(UUID.randomUUID()).label("T1").capacity(2).minPartySize(1).shape(TableShapeEnum.RECT).build();
            when(tableEntityRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(t), pageable, 1));
            Page<TableEntity> result = service.list(pageable);
            assertEquals(1, result.getContent().size());
            assertEquals("T1", result.getContent().get(0).getLabel());
        }
    }

    @Nested
    @DisplayName("getById")
    class GetById {

        @Test
        @DisplayName("returns present when found")
        void returnsPresentWhenFound() {
            UUID id = UUID.randomUUID();
            TableEntity t = TableEntity.builder().id(id).label("Laud 5").build();
            when(tableEntityRepository.findById(id)).thenReturn(Optional.of(t));
            assertTrue(service.getById(id).isPresent());
            assertEquals("Laud 5", service.getById(id).get().getLabel());
        }

        @Test
        @DisplayName("returns empty when not found")
        void returnsEmptyWhenNotFound() {
            when(tableEntityRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
            assertTrue(service.getById(UUID.randomUUID()).isEmpty());
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("updates and returns when id matches")
        void updatesWhenIdMatches() {
            UUID id = UUID.randomUUID();
            UpdateTableEntityRequest request = new UpdateTableEntityRequest();
            request.setId(id);
            request.setLabel("Laud 1 uuendatud");
            request.setCapacity(6);
            request.setMinPartySize(2);
            request.setShape(TableShapeEnum.OVAL);
            request.setX(15);
            request.setY(25);
            request.setWidth(100);
            request.setHeight(140);
            request.setRotationDegree(90);
            request.setActive(true);
            request.setAdjacentTableIds(List.of());

            TableEntity existing = TableEntity.builder().id(id).label("Laud 1").capacity(4).adjacentTables(new HashSet<>()).build();
            when(tableEntityRepository.findById(id)).thenReturn(Optional.of(existing));
            when(tableEntityRepository.save(any(TableEntity.class))).thenAnswer(inv -> inv.getArgument(0));

            TableEntity result = service.update(id, request);

            assertEquals("Laud 1 uuendatud", result.getLabel());
            assertEquals(6, result.getCapacity());
        }

        @Test
        @DisplayName("throws TableEntityUpdateException when request id is null")
        void throwsWhenRequestIdNull() {
            UpdateTableEntityRequest request = new UpdateTableEntityRequest();
            request.setId(null);
            assertThrows(TableEntityUpdateException.class, () -> service.update(UUID.randomUUID(), request));
            verify(tableEntityRepository, never()).save(any());
        }

        @Test
        @DisplayName("throws TableEntityUpdateException when path id and request id differ")
        void throwsWhenIdMismatch() {
            UpdateTableEntityRequest request = new UpdateTableEntityRequest();
            request.setId(UUID.randomUUID());
            assertThrows(TableEntityUpdateException.class, () -> service.update(UUID.randomUUID(), request));
            verify(tableEntityRepository, never()).save(any());
        }

        @Test
        @DisplayName("throws TableEntityNotFoundException when not found")
        void throwsWhenNotFound() {
            UUID id = UUID.randomUUID();
            UpdateTableEntityRequest request = new UpdateTableEntityRequest();
            request.setId(id);
            when(tableEntityRepository.findById(id)).thenReturn(Optional.empty());
            assertThrows(TableEntityNotFoundException.class, () -> service.update(id, request));
            verify(tableEntityRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("deletes when exists")
        void deletesWhenExists() {
            UUID id = UUID.randomUUID();
            TableEntity t = TableEntity.builder().id(id).label("T").adjacentTables(new HashSet<>()).build();
            when(tableEntityRepository.findById(id)).thenReturn(Optional.of(t));
            service.delete(id);
            verify(tableEntityRepository).delete(t);
        }

        @Test
        @DisplayName("throws TableEntityNotFoundException when not found")
        void throwsWhenNotFound() {
            UUID id = UUID.randomUUID();
            when(tableEntityRepository.findById(id)).thenReturn(Optional.empty());
            assertThrows(TableEntityNotFoundException.class, () -> service.delete(id));
            verify(tableEntityRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("searchAvailableTables")
    class SearchAvailableTables {

        @Test
        @DisplayName("delegates to repository search")
        void delegatesToRepository() {
            Pageable pageable = PageRequest.of(0, 10);
            TableEntity t = TableEntity.builder().id(UUID.randomUUID()).label("Laud").build();
            when(tableEntityRepository.searchTableEntities(eq("laud"), eq(pageable))).thenReturn(new PageImpl<>(List.of(t), pageable, 1));
            Page<TableEntity> result = service.searchAvailableTables("laud", pageable);
            assertEquals(1, result.getContent().size());
        }
    }

    @Nested
    @DisplayName("findTablesWithAvailability")
    class FindTablesWithAvailability {

        private static final LocalDateTime START = LocalDateTime.of(2025, 3, 15, 12, 0);
        private static final LocalDateTime END = LocalDateTime.of(2025, 3, 15, 14, 0);

        @Test
        @DisplayName("throws when both seatingPlanId and zoneId are null")
        void throwsWhenBothNull() {
            assertThrows(IllegalArgumentException.class, () ->
                    service.findTablesWithAvailability(null, null, 2, START, END, null));
            verify(tableEntityRepository, never()).findByZoneIdAndActiveTrueWithAdjacent(any());
            verify(tableEntityRepository, never()).findBySeatingPlanIdAndActiveTrueWithAdjacent(any());
        }

        @Test
        @DisplayName("returns empty list when outside opening hours")
        void returnsEmptyWhenOutsideHours() {
            UUID zoneId = UUID.randomUUID();
            when(restaurantHoursService.isWithinOpeningHours(START, END)).thenReturn(false);

            List<TableAvailabilityItemDto> result = service.findTablesWithAvailability(null, zoneId, 2, START, END, null);

            assertTrue(result.isEmpty());
            verify(tableEntityRepository, never()).findByZoneIdAndActiveTrueWithAdjacent(any());
        }

        @Test
        @DisplayName("returns mapped DTOs when by zoneId and within hours")
        void returnsDtosByZoneId() {
            UUID zoneId = UUID.randomUUID();
            when(restaurantHoursService.isWithinOpeningHours(START, END)).thenReturn(true);
            TableEntity t = TableEntity.builder().id(UUID.randomUUID()).label("T1").capacity(4).minPartySize(1).zone(Zone.builder().id(zoneId).name("Siseala").type(ZoneTypeEnum.INDOOR).build()).adjacentTables(new HashSet<>()).build();
            when(tableEntityRepository.findByZoneIdAndActiveTrueWithAdjacent(zoneId)).thenReturn(List.of(t));
            when(bookingTableRepository.findTableEntityIdsBookedBetween(START, END)).thenReturn(List.of());
            TableAvailabilityItemDto dto = new TableAvailabilityItemDto();
            dto.setLabel("T1");
            dto.setAvailable(true);
            when(tableEntityMapper.toTableAvailabilityItemDto(eq(t), eq(true), any())).thenReturn(dto);

            List<TableAvailabilityItemDto> result = service.findTablesWithAvailability(null, zoneId, 2, START, END, null);

            assertFalse(result.isEmpty());
            assertEquals("T1", result.get(0).getLabel());
            assertTrue(result.get(0).isAvailable());
        }

        @Test
        @DisplayName("uses seatingPlanId when zoneId is null")
        void usesSeatingPlanIdWhenZoneIdNull() {
            UUID planId = UUID.randomUUID();
            when(restaurantHoursService.isWithinOpeningHours(START, END)).thenReturn(true);
            when(tableEntityRepository.findBySeatingPlanIdAndActiveTrueWithAdjacent(planId)).thenReturn(List.of());
            when(bookingTableRepository.findTableEntityIdsBookedBetween(START, END)).thenReturn(List.of());

            List<TableAvailabilityItemDto> result = service.findTablesWithAvailability(planId, null, 2, START, END, null);

            verify(tableEntityRepository).findBySeatingPlanIdAndActiveTrueWithAdjacent(planId);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("excludes table when capacity < partySize")
        void excludesTableWhenCapacityTooSmall() {
            UUID zoneId = UUID.randomUUID();
            when(restaurantHoursService.isWithinOpeningHours(START, END)).thenReturn(true);
            TableEntity small = TableEntity.builder().id(UUID.randomUUID()).label("T2").capacity(1).minPartySize(1).zone(Zone.builder().id(zoneId).name("Z").type(ZoneTypeEnum.INDOOR).build()).adjacentTables(new HashSet<>()).build();
            when(tableEntityRepository.findByZoneIdAndActiveTrueWithAdjacent(zoneId)).thenReturn(List.of(small));
            when(bookingTableRepository.findTableEntityIdsBookedBetween(START, END)).thenReturn(List.of());

            List<TableAvailabilityItemDto> result = service.findTablesWithAvailability(null, zoneId, 2, START, END, null);

            assertTrue(result.stream().noneMatch(dto -> dto.getLabel() != null && dto.getLabel().equals("T2") && !dto.isCombined()));
        }

        @Test
        @DisplayName("excludes table when minPartySize > partySize")
        void excludesTableWhenMinPartySizeTooLarge() {
            UUID zoneId = UUID.randomUUID();
            when(restaurantHoursService.isWithinOpeningHours(START, END)).thenReturn(true);
            TableEntity largeMin = TableEntity.builder().id(UUID.randomUUID()).label("T3").capacity(6).minPartySize(4).zone(Zone.builder().id(zoneId).name("Z").type(ZoneTypeEnum.INDOOR).build()).adjacentTables(new HashSet<>()).build();
            when(tableEntityRepository.findByZoneIdAndActiveTrueWithAdjacent(zoneId)).thenReturn(List.of(largeMin));
            when(bookingTableRepository.findTableEntityIdsBookedBetween(START, END)).thenReturn(List.of());

            List<TableAvailabilityItemDto> result = service.findTablesWithAvailability(null, zoneId, 2, START, END, null);

            assertTrue(result.stream().noneMatch(dto -> "T3".equals(dto.getLabel()) && !dto.isCombined()));
        }

        @Test
        @DisplayName("excludes single table when empty seats exceed MAX_EMPTY_SEATS (2)")
        void excludesWhenTooManyEmptySeats() {
            UUID zoneId = UUID.randomUUID();
            when(restaurantHoursService.isWithinOpeningHours(START, END)).thenReturn(true);
            TableEntity big = TableEntity.builder().id(UUID.randomUUID()).label("T4").capacity(6).minPartySize(1).zone(Zone.builder().id(zoneId).name("Z").type(ZoneTypeEnum.INDOOR).build()).adjacentTables(new HashSet<>()).build();
            when(tableEntityRepository.findByZoneIdAndActiveTrueWithAdjacent(zoneId)).thenReturn(List.of(big));
            when(bookingTableRepository.findTableEntityIdsBookedBetween(START, END)).thenReturn(List.of());

            List<TableAvailabilityItemDto> result = service.findTablesWithAvailability(null, zoneId, 2, START, END, null);

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("marks table as unavailable when booked in slot")
        void marksBookedTableUnavailable() {
            UUID zoneId = UUID.randomUUID();
            UUID tableId = UUID.randomUUID();
            when(restaurantHoursService.isWithinOpeningHours(START, END)).thenReturn(true);
            TableEntity t = TableEntity.builder().id(tableId).label("T5").capacity(4).minPartySize(1).zone(Zone.builder().id(zoneId).name("Z").type(ZoneTypeEnum.INDOOR).build()).adjacentTables(new HashSet<>()).build();
            when(tableEntityRepository.findByZoneIdAndActiveTrueWithAdjacent(zoneId)).thenReturn(List.of(t));
            when(bookingTableRepository.findTableEntityIdsBookedBetween(START, END)).thenReturn(List.of(tableId));
            TableAvailabilityItemDto dto = new TableAvailabilityItemDto();
            dto.setLabel("T5");
            dto.setAvailable(false);
            when(tableEntityMapper.toTableAvailabilityItemDto(eq(t), eq(false), isNull())).thenReturn(dto);

            List<TableAvailabilityItemDto> result = service.findTablesWithAvailability(null, zoneId, 2, START, END, null);

            assertEquals(1, result.size());
            assertFalse(result.get(0).isAvailable());
        }

        @Test
        @DisplayName("includes combined option when two adjacent tables free and fit party")
        void includesCombinedWhenAdjacentFree() {
            UUID zoneId = UUID.randomUUID();
            UUID id1 = UUID.randomUUID();
            UUID id2 = UUID.randomUUID();
            Zone z = Zone.builder().id(zoneId).name("Z").type(ZoneTypeEnum.INDOOR).build();
            TableEntity t1 = TableEntity.builder().id(id1).label("A").capacity(2).minPartySize(1).shape(TableShapeEnum.RECT).x(0).y(0).width(60).height(80).rotationDegree(0).zone(z).adjacentTables(new HashSet<>()).build();
            TableEntity t2 = TableEntity.builder().id(id2).label("B").capacity(2).minPartySize(1).shape(TableShapeEnum.RECT).x(0).y(0).width(60).height(80).rotationDegree(0).zone(z).adjacentTables(new HashSet<>()).build();
            t1.getAdjacentTables().add(t2);
            t2.getAdjacentTables().add(t1);
            when(restaurantHoursService.isWithinOpeningHours(START, END)).thenReturn(true);
            when(tableEntityRepository.findByZoneIdAndActiveTrueWithAdjacent(zoneId)).thenReturn(List.of(t1, t2));
            when(bookingTableRepository.findTableEntityIdsBookedBetween(START, END)).thenReturn(List.of());

            List<TableAvailabilityItemDto> result = service.findTablesWithAvailability(null, zoneId, 3, START, END, null);

            List<TableAvailabilityItemDto> combined = result.stream().filter(TableAvailabilityItemDto::isCombined).toList();
            assertFalse(combined.isEmpty());
            TableAvailabilityItemDto combinedOption = combined.get(0);
            assertTrue(combinedOption.getTableIds().contains(id1) && combinedOption.getTableIds().contains(id2));
            assertEquals(4, combinedOption.getCapacity());
            assertTrue(combinedOption.isAvailable());
        }

        @Test
        @DisplayName("preferredFeatureIds null does not break")
        void preferredFeatureIdsNull() {
            UUID zoneId = UUID.randomUUID();
            when(restaurantHoursService.isWithinOpeningHours(START, END)).thenReturn(true);
            TableEntity t = TableEntity.builder().id(UUID.randomUUID()).label("T").capacity(4).minPartySize(1).zone(Zone.builder().id(zoneId).name("Z").type(ZoneTypeEnum.INDOOR).build()).adjacentTables(new HashSet<>()).build();
            when(tableEntityRepository.findByZoneIdAndActiveTrueWithAdjacent(zoneId)).thenReturn(List.of(t));
            when(bookingTableRepository.findTableEntityIdsBookedBetween(START, END)).thenReturn(List.of());
            when(tableEntityMapper.toTableAvailabilityItemDto(any(), anyBoolean(), any())).thenReturn(new TableAvailabilityItemDto());

            assertDoesNotThrow(() -> service.findTablesWithAvailability(null, zoneId, 2, START, END, null));
        }

        @Test
        @DisplayName("available tables ordered before unavailable, then by score descending")
        void orderingAvailableFirstThenByScore() {
            UUID zoneId = UUID.randomUUID();
            when(restaurantHoursService.isWithinOpeningHours(START, END)).thenReturn(true);
            TableEntity t1 = TableEntity.builder().id(UUID.randomUUID()).label("T1").capacity(4).minPartySize(1).zone(Zone.builder().id(zoneId).name("Z").type(ZoneTypeEnum.INDOOR).build()).adjacentTables(new HashSet<>()).build();
            TableEntity t2 = TableEntity.builder().id(UUID.randomUUID()).label("T2").capacity(4).minPartySize(1).zone(Zone.builder().id(zoneId).name("Z").type(ZoneTypeEnum.INDOOR).build()).adjacentTables(new HashSet<>()).build();
            when(tableEntityRepository.findByZoneIdAndActiveTrueWithAdjacent(zoneId)).thenReturn(List.of(t1, t2));
            when(bookingTableRepository.findTableEntityIdsBookedBetween(START, END)).thenReturn(List.of(t2.getId()));
            when(tableEntityMapper.toTableAvailabilityItemDto(eq(t1), eq(true), any())).thenAnswer(inv -> {
                TableAvailabilityItemDto d = new TableAvailabilityItemDto();
                d.setAvailable(true);
                d.setRecommendationScore(98);
                d.setLabel("T1");
                return d;
            });
            when(tableEntityMapper.toTableAvailabilityItemDto(eq(t2), eq(false), isNull())).thenAnswer(inv -> {
                TableAvailabilityItemDto d = new TableAvailabilityItemDto();
                d.setAvailable(false);
                d.setLabel("T2");
                return d;
            });

            List<TableAvailabilityItemDto> result = service.findTablesWithAvailability(null, zoneId, 2, START, END, null);

            assertTrue(result.get(0).isAvailable());
            assertFalse(result.get(result.size() - 1).isAvailable());
        }
    }

    @Nested
    @DisplayName("validateTablesAdjacent")
    class ValidateTablesAdjacent {

        @Test
        @DisplayName("does nothing when tableIds is null")
        void noOpWhenNull() {
            assertDoesNotThrow(() -> service.validateTablesAdjacent(null));
            verify(tableEntityRepository, never()).findByIdInWithAdjacent(any());
        }

        @Test
        @DisplayName("does nothing when tableIds has zero or one element")
        void noOpWhenZeroOrOne() {
            assertDoesNotThrow(() -> service.validateTablesAdjacent(Set.of()));
            assertDoesNotThrow(() -> service.validateTablesAdjacent(Set.of(UUID.randomUUID())));
            verify(tableEntityRepository, never()).findByIdInWithAdjacent(any());
        }

        @Test
        @DisplayName("throws RestaurantBookingException when not all tables found")
        void throwsWhenNotAllFound() {
            UUID id1 = UUID.randomUUID();
            UUID id2 = UUID.randomUUID();
            TableEntity t1 = TableEntity.builder().id(id1).adjacentTables(new HashSet<>()).build();
            when(tableEntityRepository.findByIdInWithAdjacent(anyList())).thenReturn(List.of(t1));

            RestaurantBookingException ex = assertThrows(RestaurantBookingException.class,
                    () -> service.validateTablesAdjacent(Set.of(id1, id2)));
            assertTrue(ex.getMessage().toLowerCase().contains("find"));
        }

        @Test
        @DisplayName("throws RestaurantBookingException when tables not adjacent")
        void throwsWhenNotAdjacent() {
            UUID id1 = UUID.randomUUID();
            UUID id2 = UUID.randomUUID();
            TableEntity t1 = TableEntity.builder().id(id1).adjacentTables(new HashSet<>()).build();
            TableEntity t2 = TableEntity.builder().id(id2).adjacentTables(new HashSet<>()).build();
            when(tableEntityRepository.findByIdInWithAdjacent(anyList())).thenReturn(List.of(t1, t2));

            RestaurantBookingException ex = assertThrows(RestaurantBookingException.class,
                    () -> service.validateTablesAdjacent(Set.of(id1, id2)));
            assertTrue(ex.getMessage().toLowerCase().contains("nearby"));
        }

        @Test
        @DisplayName("does not throw when tables are adjacent")
        void successWhenAdjacent() {
            UUID id1 = UUID.randomUUID();
            UUID id2 = UUID.randomUUID();
            TableEntity t1 = TableEntity.builder().id(id1).adjacentTables(new HashSet<>()).build();
            TableEntity t2 = TableEntity.builder().id(id2).adjacentTables(new HashSet<>()).build();
            t1.getAdjacentTables().add(t2);
            t2.getAdjacentTables().add(t1);
            when(tableEntityRepository.findByIdInWithAdjacent(anyList())).thenReturn(List.of(t1, t2));

            assertDoesNotThrow(() -> service.validateTablesAdjacent(Set.of(id1, id2)));
        }

        @Test
        @DisplayName("does not throw when three tables in chain (A-B, B-C)")
        void successWhenThreeTablesChained() {
            UUID idA = UUID.randomUUID();
            UUID idB = UUID.randomUUID();
            UUID idC = UUID.randomUUID();
            TableEntity ta = TableEntity.builder().id(idA).adjacentTables(new HashSet<>()).build();
            TableEntity tb = TableEntity.builder().id(idB).adjacentTables(new HashSet<>()).build();
            TableEntity tc = TableEntity.builder().id(idC).adjacentTables(new HashSet<>()).build();
            ta.getAdjacentTables().add(tb);
            tb.getAdjacentTables().add(ta);
            tb.getAdjacentTables().add(tc);
            tc.getAdjacentTables().add(tb);
            when(tableEntityRepository.findByIdInWithAdjacent(anyList())).thenReturn(List.of(ta, tb, tc));

            assertDoesNotThrow(() -> service.validateTablesAdjacent(Set.of(idA, idB, idC)));
        }
    }

    @Nested
    @DisplayName("create edge cases")
    class CreateEdgeCases {

        @Test
        @DisplayName("null adjacentTableIds treated as empty")
        void nullAdjacentIdsAsEmpty() {
            CreateTableEntityRequest request = new CreateTableEntityRequest();
            request.setLabel("Laud");
            request.setCapacity(2);
            request.setMinPartySize(1);
            request.setShape(TableShapeEnum.RECT);
            request.setX(0);
            request.setY(0);
            request.setWidth(60);
            request.setHeight(80);
            request.setRotationDegree(0);
            request.setActive(true);
            request.setAdjacentTableIds(null);

            TableEntity saved = TableEntity.builder().id(UUID.randomUUID()).label("Laud").adjacentTables(new HashSet<>()).build();
            when(tableEntityRepository.save(any(TableEntity.class))).thenReturn(saved);

            assertDoesNotThrow(() -> service.create(request));
            verify(tableEntityRepository, atLeast(1)).save(any(TableEntity.class));
        }
    }

    @Nested
    @DisplayName("update edge cases")
    class UpdateEdgeCases {

        @Test
        @DisplayName("clearing adjacent tables when empty list")
        void clearAdjacentTables() {
            UUID id = UUID.randomUUID();
            UUID otherId = UUID.randomUUID();
            TableEntity other = TableEntity.builder().id(otherId).adjacentTables(new HashSet<>()).build();
            TableEntity existing = TableEntity.builder().id(id).label("T").capacity(2).minPartySize(1).shape(TableShapeEnum.RECT).x(0).y(0).width(60).height(80).rotationDegree(0).active(true).adjacentTables(new HashSet<>()).build();
            existing.getAdjacentTables().add(other);
            other.getAdjacentTables().add(existing);

            UpdateTableEntityRequest request = new UpdateTableEntityRequest();
            request.setId(id);
            request.setLabel("T");
            request.setCapacity(2);
            request.setMinPartySize(1);
            request.setShape(TableShapeEnum.RECT);
            request.setX(0);
            request.setY(0);
            request.setWidth(60);
            request.setHeight(80);
            request.setRotationDegree(0);
            request.setActive(true);
            request.setAdjacentTableIds(List.of());

            when(tableEntityRepository.findById(id)).thenReturn(Optional.of(existing));
            when(tableEntityRepository.save(any(TableEntity.class))).thenAnswer(inv -> inv.getArgument(0));

            TableEntity result = service.update(id, request);

            assertTrue(result.getAdjacentTables().isEmpty());
            verify(tableEntityRepository, atLeast(1)).save(any(TableEntity.class));
        }
    }
}
