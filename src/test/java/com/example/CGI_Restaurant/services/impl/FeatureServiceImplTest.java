package com.example.CGI_Restaurant.services.impl;

/**
 * @author AI (assisted). Used my BookingSeviceImplTest.
 */

import com.example.CGI_Restaurant.domain.entities.Feature;
import com.example.CGI_Restaurant.domain.entities.FeatureCodeEnum;
import com.example.CGI_Restaurant.domain.createRequests.CreateFeatureRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateFeatureRequest;
import com.example.CGI_Restaurant.exceptions.notFoundExceptions.FeatureNotFoundException;
import com.example.CGI_Restaurant.exceptions.updateException.FeatureUpdateException;
import com.example.CGI_Restaurant.repositories.FeatureRepository;
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
class FeatureServiceImplTest {

    @Mock
    private FeatureRepository featureRepository;

    @InjectMocks
    private FeatureServiceImpl service;

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("saves new feature with request data")
        void createsFeatureWithRequestData() {
            CreateFeatureRequest request = new CreateFeatureRequest();
            request.setCode(FeatureCodeEnum.WINDOW);
            request.setName("Akna vaade");

            Feature saved = Feature.builder().id(UUID.randomUUID()).code(FeatureCodeEnum.WINDOW).name("Akna vaade").build();
            when(featureRepository.save(any(Feature.class))).thenReturn(saved);

            Feature result = service.create(request);

            assertNotNull(result);
            assertEquals(FeatureCodeEnum.WINDOW, result.getCode());
            ArgumentCaptor<Feature> captor = ArgumentCaptor.forClass(Feature.class);
            verify(featureRepository).save(captor.capture());
            assertEquals("Akna vaade", captor.getValue().getName());
        }
    }

    @Nested
    @DisplayName("list")
    class ListMethod {

        @Test
        @DisplayName("returns page from repository")
        void returnsPageFromRepository() {
            Pageable pageable = PageRequest.of(0, 10);
            Feature f = Feature.builder().id(UUID.randomUUID()).code(FeatureCodeEnum.QUIET).name("Vaikne nurk").build();
            when(featureRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(f), pageable, 1));
            Page<Feature> result = service.list(pageable);
            assertEquals(1, result.getContent().size());
            assertEquals(FeatureCodeEnum.QUIET, result.getContent().get(0).getCode());
        }
    }

    @Nested
    @DisplayName("getById")
    class GetById {

        @Test
        @DisplayName("returns present when found")
        void returnsPresentWhenFound() {
            UUID id = UUID.randomUUID();
            Feature feature = Feature.builder().id(id).code(FeatureCodeEnum.ACCESSIBLE).name("Käigurada").build();
            when(featureRepository.findById(id)).thenReturn(Optional.of(feature));
            assertTrue(service.getById(id).isPresent());
            assertEquals("Käigurada", service.getById(id).get().getName());
        }

        @Test
        @DisplayName("returns empty when not found")
        void returnsEmptyWhenNotFound() {
            when(featureRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
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
            UpdateFeatureRequest request = new UpdateFeatureRequest();
            request.setId(id);
            request.setCode(FeatureCodeEnum.NEAR_BAR);
            request.setName("Lähedal baarile");

            Feature existing = Feature.builder().id(id).code(FeatureCodeEnum.WINDOW).name("Akna vaade").build();
            when(featureRepository.findById(id)).thenReturn(Optional.of(existing));
            when(featureRepository.save(any(Feature.class))).thenAnswer(inv -> inv.getArgument(0));

            Feature result = service.update(id, request);

            assertEquals(FeatureCodeEnum.NEAR_BAR, result.getCode());
            assertEquals("Lähedal baarile", result.getName());
        }

        @Test
        @DisplayName("throws FeatureUpdateException when request id null")
        void throwsWhenRequestIdNull() {
            UpdateFeatureRequest request = new UpdateFeatureRequest();
            request.setId(null);
            request.setName("Nimi");
            assertThrows(FeatureUpdateException.class, () -> service.update(UUID.randomUUID(), request));
            verify(featureRepository, never()).save(any());
        }

        @Test
        @DisplayName("throws FeatureUpdateException when id mismatch")
        void throwsWhenIdMismatch() {
            UpdateFeatureRequest request = new UpdateFeatureRequest();
            request.setId(UUID.randomUUID());
            request.setName("Nimi");
            assertThrows(FeatureUpdateException.class, () -> service.update(UUID.randomUUID(), request));
            verify(featureRepository, never()).save(any());
        }

        @Test
        @DisplayName("throws FeatureNotFoundException when not found")
        void throwsWhenNotFound() {
            UUID id = UUID.randomUUID();
            UpdateFeatureRequest request = new UpdateFeatureRequest();
            request.setId(id);
            request.setName("Nimi");
            when(featureRepository.findById(id)).thenReturn(Optional.empty());
            assertThrows(FeatureNotFoundException.class, () -> service.update(id, request));
            verify(featureRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("deletes when exists")
        void deletesWhenExists() {
            UUID id = UUID.randomUUID();
            Feature feature = Feature.builder().id(id).code(FeatureCodeEnum.CENTER).name("Keskus").build();
            when(featureRepository.findById(id)).thenReturn(Optional.of(feature));
            service.delete(id);
            verify(featureRepository).delete(feature);
        }

        @Test
        @DisplayName("throws FeatureNotFoundException when not found")
        void throwsWhenNotFound() {
            UUID id = UUID.randomUUID();
            when(featureRepository.findById(id)).thenReturn(Optional.empty());
            assertThrows(FeatureNotFoundException.class, () -> service.delete(id));
            verify(featureRepository, never()).delete(any());
        }
    }
}
