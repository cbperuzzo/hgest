package com.lumem.hgest.repository;

import com.lumem.hgest.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalTime;

public interface ServiceRepository extends JpaRepository<Service,Long> {
    @Modifying
    @Query("""
        UPDATE service s
        SET s.closeDate = :date,
            s.closeTime = :time,
            s.closed = true
        WHERE s.id = :serviceId
            
            
    """)
    void closeService(long serviceId, LocalTime time, LocalDate date);

}
