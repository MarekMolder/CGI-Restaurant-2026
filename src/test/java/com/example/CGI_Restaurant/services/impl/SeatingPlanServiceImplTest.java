package com.example.CGI_Restaurant.services.impl;

/**
 * @author AI (assisted). Used my BookingSeviceImplTest.
 */

import com.example.CGI_Restaurant.domain.entities.SeatingPlan;
import com.example.CGI_Restaurant.domain.entities.SeatingPlanTypeEnum;
import com.example.CGI_Restaurant.domain.createRequests.CreateSeatingPlanRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateSeatingPlanRequest;
import com.example.CGI_Restaurant.exceptions.notFoundExceptions.SeatingPlanNotFoundException;
import com.example.CGI_Restaurant.exceptions.updateException.SeatingPlanUpdateException;
import com.example.CGI_Restaurant.repositories.SeatingPlanRepository;
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
class SeatingPlanServiceImplTest {

    @Mock
    private SeatingPlanRepository seatingPlanRepository;

    @InjectMocks
    private SeatingPlanServiceImpl service;

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("saves new seating plan with all request fields")
        void createsWithRequestData() {
            CreateSeatingPlanRequest request = new CreateSeatingPlanRequest();
            request.setName("Peosaal");
            request.setType(SeatingPlanTypeEnum.FLOOR_1);
            request.setWidth(800.0);
            request.setHeight(600.0);
            request.setBackgroundSVG("<svg></svg>");
            request.setActive(true);
            request.setVersion(1);

            SeatingPlan saved = SeatingPlan.builder().id(UUID.randomUUID()).name("Peosaal").type(SeatingPlanTypeEnum.FLOOR_1)
                    .width(800).height(600).active(true).version(1).build();
            when(seatingPlanRepository.save(any(SeatingPlan.class))).thenReturn(saved);

            SeatingPlan result = service.create(request);

            assertNotNull(result);
            assertEquals("Peosaal", result.getName());
            ArgumentCaptor<SeatingPlan> captor = ArgumentCaptor.forClass(SeatingPlan.class);
            verify(seatingPlanRepository).save(captor.capture());
            assertEquals(800.0, captor.getValue().getWidth());
            assertEquals(600.0, captor.getValue().getHeight());
            assertTrue(captor.getValue().isActive());
        }
    }

    @Nested
    @DisplayName("list")
    class ListMethod {

        @Test
        @DisplayName("returns page from repository")
        void returnsPageFromRepository() {
            Pageable pageable = PageRequest.of(0, 10);
            SeatingPlan plan = SeatingPlan.builder().id(UUID.randomUUID()).name("P").type(SeatingPlanTypeEnum.FLOOR_1).width(100).height(100).active(true).version(1).build();
            when(seatingPlanRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(plan), pageable, 1));
            assertEquals(1, service.list(pageable).getContent().size());
        }
    }

    @Nested
    @DisplayName("getById")
    class GetById {

        @Test
        @DisplayName("returns present when found")
        void returnsPresentWhenFound() {
            UUID id = UUID.randomUUID();
            SeatingPlan plan = SeatingPlan.builder().id(id).name("P").type(SeatingPlanTypeEnum.OUTDOOR).width(200).height(150).active(false).version(0).build();
            when(seatingPlanRepository.findById(id)).thenReturn(Optional.of(plan));
            assertTrue(service.getById(id).isPresent());
            assertEquals(SeatingPlanTypeEnum.OUTDOOR, service.getById(id).get().getType());
        }

        @Test
        @DisplayName("returns empty when not found")
        void returnsEmptyWhenNotFound() {
            when(seatingPlanRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
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
            UpdateSeatingPlanRequest request = new UpdateSeatingPlanRequest();
            request.setId(id);
            request.setName("Peosaal uuendatud");
            request.setType(SeatingPlanTypeEnum.FLOOR_2);
            request.setWidth(900.0);
            request.setHeight(700.0);
            request.setActive(false);
            request.setVersion(2);

            SeatingPlan existing = SeatingPlan.builder().id(id).name("P").type(SeatingPlanTypeEnum.FLOOR_1).width(100).height(100).active(true).version(1).build();
            when(seatingPlanRepository.findById(id)).thenReturn(Optional.of(existing));
            when(seatingPlanRepository.save(any(SeatingPlan.class))).thenAnswer(inv -> inv.getArgument(0));

            SeatingPlan result = service.update(id, request);

            assertEquals("Peosaal uuendatud", result.getName());
            assertEquals(SeatingPlanTypeEnum.FLOOR_2, result.getType());
            assertEquals(2, result.getVersion());
        }

        @Test
        @DisplayName("throws SeatingPlanUpdateException when request id null")
        void throwsWhenRequestIdNull() {
            UpdateSeatingPlanRequest request = new UpdateSeatingPlanRequest();
            request.setId(null);
            request.setName("Nimi");
            assertThrows(SeatingPlanUpdateException.class, () -> service.update(UUID.randomUUID(), request));
            verify(seatingPlanRepository, never()).save(any());
        }

        @Test
        @DisplayName("throws SeatingPlanUpdateException when id mismatch")
        void throwsWhenIdMismatch() {
            UpdateSeatingPlanRequest request = new UpdateSeatingPlanRequest();
            request.setId(UUID.randomUUID());
            request.setName("Nimi");
            assertThrows(SeatingPlanUpdateException.class, () -> service.update(UUID.randomUUID(), request));
            verify(seatingPlanRepository, never()).save(any());
        }

        @Test
        @DisplayName("throws SeatingPlanNotFoundException when not found")
        void throwsWhenNotFound() {
            UUID id = UUID.randomUUID();
            UpdateSeatingPlanRequest request = new UpdateSeatingPlanRequest();
            request.setId(id);
            request.setName("Nimi");
            when(seatingPlanRepository.findById(id)).thenReturn(Optional.empty());
            assertThrows(SeatingPlanNotFoundException.class, () -> service.update(id, request));
            verify(seatingPlanRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("deletes when exists")
        void deletesWhenExists() {
            UUID id = UUID.randomUUID();
            SeatingPlan plan = SeatingPlan.builder().id(id).name("P").type(SeatingPlanTypeEnum.FLOOR_1).width(1).height(1).active(true).version(1).build();
            when(seatingPlanRepository.findById(id)).thenReturn(Optional.of(plan));
            service.delete(id);
            verify(seatingPlanRepository).delete(plan);
        }

        @Test
        @DisplayName("throws SeatingPlanNotFoundException when not found")
        void throwsWhenNotFound() {
            UUID id = UUID.randomUUID();
            when(seatingPlanRepository.findById(id)).thenReturn(Optional.empty());
            assertThrows(SeatingPlanNotFoundException.class, () -> service.delete(id));
            verify(seatingPlanRepository, never()).delete(any());
        }
    }
}
