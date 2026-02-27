package com.example.CGI_Restaurant.services.impl;

import com.example.CGI_Restaurant.domain.entities.TableEntity;
import com.example.CGI_Restaurant.domain.createRequests.CreateTableEntityRequest;
import com.example.CGI_Restaurant.domain.updateRequests.UpdateTableEntityRequest;
import com.example.CGI_Restaurant.exceptions.notFoundExceptions.TableEntityNotFoundException;
import com.example.CGI_Restaurant.exceptions.updateException.TableEntityUpdateException;
import com.example.CGI_Restaurant.repositories.TableEntityRepository;
import com.example.CGI_Restaurant.services.TableEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TableEntityServiceImpl implements TableEntityService {

    private final TableEntityRepository tableEntityRepository;

    @Override
    public TableEntity create(CreateTableEntityRequest request) {
        TableEntity entity = new TableEntity();
        entity.setLabel(request.getLabel());
        entity.setCapacity(request.getCapacity());
        entity.setMinPartySize(request.getMinPartySize());
        entity.setShape(request.getShape());
        entity.setX(request.getX());
        entity.setY(request.getY());
        entity.setWidth(request.getWidth());
        entity.setHeight(request.getHeight());
        entity.setRotationDegree(request.getRotationDegree());
        entity.setActive(request.isActive());
        return tableEntityRepository.save(entity);
    }

    @Override
    public Page<TableEntity> list(Pageable pageable) {
        return tableEntityRepository.findAll(pageable);
    }

    @Override
    public Optional<TableEntity> getById(UUID id) {
        return tableEntityRepository.findById(id);
    }

    @Override
    @Transactional
    public TableEntity update(UUID id, UpdateTableEntityRequest request) {
        if (request.getId() == null || !id.equals(request.getId())) {
            throw new TableEntityUpdateException("Table entity ID mismatch");
        }
        TableEntity entity = tableEntityRepository.findById(id)
                .orElseThrow(() -> new TableEntityNotFoundException("Table entity with ID '%s' not found".formatted(id)));
        entity.setLabel(request.getLabel());
        entity.setCapacity(request.getCapacity());
        entity.setMinPartySize(request.getMinPartySize());
        entity.setShape(request.getShape());
        entity.setX(request.getX());
        entity.setY(request.getY());
        entity.setWidth(request.getWidth());
        entity.setHeight(request.getHeight());
        entity.setRotationDegree(request.getRotationDegree());
        entity.setActive(request.isActive());
        return tableEntityRepository.save(entity);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        TableEntity entity = tableEntityRepository.findById(id)
                .orElseThrow(() -> new TableEntityNotFoundException("Table entity with ID '%s' not found".formatted(id)));
        tableEntityRepository.delete(entity);
    }
}
