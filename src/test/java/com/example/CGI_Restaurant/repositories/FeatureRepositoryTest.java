package com.example.CGI_Restaurant.repositories;

/**
 * @author AI (assisted). Used my BookingRepositoryTest + UserRepositoryTest.
 */

import com.example.CGI_Restaurant.config.JpaTestConfig;
import com.example.CGI_Restaurant.domain.entities.Feature;
import com.example.CGI_Restaurant.domain.entities.FeatureCodeEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(JpaTestConfig.class)
@ActiveProfiles("test")
class FeatureRepositoryTest {

    @Autowired
    private FeatureRepository repository;

    private static Feature newFeature(FeatureCodeEnum code, String displayName) {
        return Feature.builder()
                .code(code)
                .name(displayName)
                .build();
    }

    @Nested
    @DisplayName("Save and find")
    class SaveAndFind {

        @Test
        @DisplayName("Saves and finds by id")
        void saveAndFindById() {
            Feature feature = repository.save(newFeature(FeatureCodeEnum.WINDOW, "Akna vaade"));
            assertNotNull(feature.getId());
            Optional<Feature> found = repository.findById(feature.getId());
            assertTrue(found.isPresent());
            assertEquals(FeatureCodeEnum.WINDOW, found.get().getCode());
            assertEquals("Akna vaade", found.get().getName());
        }

        @Test
        @DisplayName("FindAll returns saved features")
        void findAllReturnsSaved() {
            repository.save(newFeature(FeatureCodeEnum.WINDOW, "Akna vaade"));
            repository.save(newFeature(FeatureCodeEnum.QUIET, "Vaikne nurk"));
            List<Feature> all = repository.findAll();
            assertTrue(all.size() >= 2);
        }

        @Test
        @DisplayName("FindById returns empty when not found")
        void findByIdReturnsEmptyWhenNotFound() {
            assertTrue(repository.findById(UUID.randomUUID()).isEmpty());
        }
    }
}
