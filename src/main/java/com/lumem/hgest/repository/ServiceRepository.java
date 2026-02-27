package com.lumem.hgest.repository;

import com.lumem.hgest.model.service.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;

public interface ServiceRepository extends JpaRepository<Service,Long> {

    Page<Service> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<Service> findByOs(long os, Pageable pageable);

    Page<Service> findByClosed(boolean closed, Pageable pageable);

    @Query("""
        SELECT s FROM service s
        WHERE (:closed IS NULL OR s.closed = :closed)
          AND (:startDate IS NULL OR s.openDate >= :startDate)
          AND (:endDate IS NULL OR s.openDate <= :endDate)
    """)
    Page<Service> findWithFilters(
            @Param("closed") Boolean closed,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

}
