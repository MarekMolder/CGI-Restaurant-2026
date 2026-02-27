package com.example.CGI_Restaurant.repositories;

import com.example.CGI_Restaurant.domain.entities.TableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TableEntityRepository extends JpaRepository<TableEntity, UUID> {
}
