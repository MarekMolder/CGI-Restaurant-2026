package com.example.CGI_Restaurant.repositories;

import com.example.CGI_Restaurant.domain.entities.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ZoneRepository extends JpaRepository<Zone, UUID> {

    @Query("SELECT DISTINCT z FROM Zone z LEFT JOIN FETCH z.features WHERE z.id IN :ids")
    List<Zone> findByIdInWithFeatures(@Param("ids") List<UUID> ids);
}
