package com.example.CGI_Restaurant.services.impl;

/**
 * @author AI (assisted). Used my BookingSeviceImplTest.
 */

import com.example.CGI_Restaurant.domain.entities.Zone;
import com.example.CGI_Restaurant.domain.entities.ZoneTypeEnum;
import com.example.CGI_Restaurant.domain.createRequests.CreateZoneRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateZoneRequest;
import com.example.CGI_Restaurant.exceptions.notFoundExceptions.ZoneNotFoundException;
import com.example.CGI_Restaurant.exceptions.updateException.ZoneUpdateException;
import com.example.CGI_Restaurant.repositories.ZoneRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ZoneServiceImplTest {

    @Mock
    private ZoneRepository zoneRepository;

    @InjectMocks
    private ZoneServiceImpl service;

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("saves new zone with request data")
        void createsZoneWithRequestData() {
            CreateZoneRequest request = new CreateZoneRequest();
            request.setName("Terrass");
            request.setType(ZoneTypeEnum.TERRACE);
            request.setColor("#2E7D32");

            Zone saved = Zone.builder().id(UUID.randomUUID()).name("Terrass").type(ZoneTypeEnum.TERRACE).color("#2E7D32").build();
            when(zoneRepository.save(any(Zone.class))).thenReturn(saved);

            Zone result = service.create(request);

            assertNotNull(result);
            assertEquals("Terrass", result.getName());
            ArgumentCaptor<Zone> captor = ArgumentCaptor.forClass(Zone.class);
            verify(zoneRepository).save(captor.capture());
            assertEquals("#2E7D32", captor.getValue().getColor());
        }
    }

    @Nested
    @DisplayName("list")
    class ListMethod {

        @Test
        @DisplayName("returns page from repository")
        void returnsPageFromRepository() {
            Pageable pageable = PageRequest.of(0, 10);
            Zone zone = Zone.builder().id(UUID.randomUUID()).name("Siseala").type(ZoneTypeEnum.INDOOR).color("#3366FF").build();
            when(zoneRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(zone), pageable, 1));

            Page<Zone> result = service.list(pageable);

            assertEquals(1, result.getContent().size());
            assertEquals("Siseala", result.getContent().get(0).getName());
        }
    }

    @Nested
    @DisplayName("getById")
    class GetById {

        @Test
        @DisplayName("returns optional with zone when found")
        void returnsPresentWhenFound() {
            UUID id = UUID.randomUUID();
            Zone zone = Zone.builder().id(id).name("Baari tsoon").type(ZoneTypeEnum.BAR).color("#000").build();
            when(zoneRepository.findById(id)).thenReturn(Optional.of(zone));

            Optional<Zone> result = service.getById(id);

            assertTrue(result.isPresent());
            assertEquals(ZoneTypeEnum.BAR, result.get().getType());
        }

        @Test
        @DisplayName("returns empty when not found")
        void returnsEmptyWhenNotFound() {
            when(zoneRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
            assertTrue(service.getById(UUID.randomUUID()).isEmpty());
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("updates and returns entity when id matches")
        void updatesWhenIdMatches() {
            UUID id = UUID.randomUUID();
            UpdateZoneRequest request = new UpdateZoneRequest();
            request.setId(id);
            request.setName("Terrass uuendatud");
            request.setType(ZoneTypeEnum.TERRACE);
            request.setColor("#1B5E20");

            Zone existing = Zone.builder().id(id).name("Terrass").type(ZoneTypeEnum.TERRACE).color("#FF0000").build();
            when(zoneRepository.findById(id)).thenReturn(Optional.of(existing));
            when(zoneRepository.save(any(Zone.class))).thenAnswer(inv -> inv.getArgument(0));

            Zone result = service.update(id, request);

            assertEquals("Terrass uuendatud", result.getName());
            assertEquals("#1B5E20", result.getColor());
        }

        @Test
        @DisplayName("throws ZoneUpdateException when request id is null")
        void throwsWhenRequestIdNull() {
            UpdateZoneRequest request = new UpdateZoneRequest();
            request.setId(null);
            request.setName("Nimi");
            assertThrows(ZoneUpdateException.class, () -> service.update(UUID.randomUUID(), request));
            verify(zoneRepository, never()).save(any());
        }

        @Test
        @DisplayName("throws ZoneUpdateException when path id and request id differ")
        void throwsWhenIdMismatch() {
            UUID pathId = UUID.randomUUID();
            UpdateZoneRequest request = new UpdateZoneRequest();
            request.setId(UUID.randomUUID());
            request.setName("Nimi");
            assertThrows(ZoneUpdateException.class, () -> service.update(pathId, request));
            verify(zoneRepository, never()).save(any());
        }

        @Test
        @DisplayName("throws ZoneNotFoundException when zone not found")
        void throwsWhenNotFound() {
            UUID id = UUID.randomUUID();
            UpdateZoneRequest request = new UpdateZoneRequest();
            request.setId(id);
            request.setName("Nimi");
            when(zoneRepository.findById(id)).thenReturn(Optional.empty());
            assertThrows(ZoneNotFoundException.class, () -> service.update(id, request));
            verify(zoneRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("deletes when zone exists")
        void deletesWhenExists() {
            UUID id = UUID.randomUUID();
            Zone zone = Zone.builder().id(id).name("Z").type(ZoneTypeEnum.INDOOR).color("#0").build();
            when(zoneRepository.findById(id)).thenReturn(Optional.of(zone));
            service.delete(id);
            verify(zoneRepository).delete(zone);
        }

        @Test
        @DisplayName("throws ZoneNotFoundException when not found")
        void throwsWhenNotFound() {
            UUID id = UUID.randomUUID();
            when(zoneRepository.findById(id)).thenReturn(Optional.empty());
            assertThrows(ZoneNotFoundException.class, () -> service.delete(id));
            verify(zoneRepository, never()).delete(any());
        }
    }
}
