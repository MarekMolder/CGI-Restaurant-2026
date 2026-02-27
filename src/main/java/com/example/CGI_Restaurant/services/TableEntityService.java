package com.example.CGI_Restaurant.services;

import com.example.CGI_Restaurant.domain.entities.TableEntity;
import com.example.CGI_Restaurant.domain.createRequests.CreateTableEntityRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateTableEntityRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface TableEntityService {
    TableEntity create(CreateTableEntityRequest request);
    Page<TableEntity> list(Pageable pageable);
    Optional<TableEntity> getById(UUID id);
    TableEntity update(UUID id, UpdateTableEntityRequest request);
    void delete(UUID id);
}
