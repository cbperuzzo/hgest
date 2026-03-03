package com.lumem.hgest.repository;

import com.lumem.hgest.model.shift.Shift;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ShiftRepository extends JpaRepository<Shift, Long> {

    @Query("""
       SELECT CASE WHEN COUNT(s) = 0 THEN true ELSE false END
       FROM Shift s
       WHERE s.storedUser.id = :userId
         AND s.closed = false
       """)
    boolean hasNoOpenShifts(@Param("userId") Long userId);


    Page<Shift> findByStoredUserId(long storedUserId, Pageable pageable);

    Page<Shift> findByStoredUserIdAndClosed(long storedUserId, boolean closed, Pageable pageable);


    @Query("""
           SELECT COALESCE(SUM(s.totalMinutes), 0)
           FROM Shift s
           WHERE s.storedUser.id = :userId
           AND s.openDate BETWEEN :start AND :end
           """)
    Long sumTotalMinutesByUserInRange(
            @Param("userId") Long userId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    Page<Shift> findByServiceIdAndOpenDateBetween(
            Long serviceId,
            LocalDate start,
            LocalDate end,
            Pageable pageable
    );

    Page<Shift> findByStoredUserIdAndOpenDateBetween(
            Long userId,
            LocalDate start,
            LocalDate end,
            Pageable pageable
    );
}
