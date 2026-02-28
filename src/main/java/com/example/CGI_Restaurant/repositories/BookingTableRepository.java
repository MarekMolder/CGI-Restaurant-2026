package com.example.CGI_Restaurant.repositories;

import com.example.CGI_Restaurant.domain.entities.BookingTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface BookingTableRepository extends JpaRepository<BookingTable, UUID> {

    @Query("SELECT bt.tableEntity.id FROM BookingTable bt WHERE bt.booking.startAt < :endAt AND bt.booking.endAt > :startAt " +
           "AND bt.booking.status NOT IN ('CANCELLED', 'COMPLETED')")
    List<UUID> findTableEntityIdsBookedBetween(@Param("startAt") LocalDateTime startAt, @Param("endAt") LocalDateTime endAt);
}
