package com.lumem.hgest.controllers;

import com.lumem.hgest.model.shift.ShiftDTO;
import com.lumem.hgest.repository.ShiftRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("api/shift")
@Transactional
public class ShiftController {

    private final ShiftRepository shiftRepository;

    public ShiftController(ShiftRepository shiftRepository) {
        this.shiftRepository = shiftRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShiftDTO> getById(@PathVariable Long id) {

        return shiftRepository.findById(id)
                .map(shift -> ResponseEntity.ok(new ShiftDTO(shift)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public Page<ShiftDTO> getByUserInRange(
            @PathVariable Long userId,
            @RequestParam(required = false) LocalDate start,
            @RequestParam(required = false) LocalDate end,
            Pageable pageable) {

        LocalDate[] range = resolveRange(start, end);

        return shiftRepository
                .findByStoredUserIdAndOpenDateBetween(
                        userId,
                        range[0],
                        range[1],
                        pageable
                )
                .map(ShiftDTO::new);
    }

    @GetMapping("/user/{userId}/total")
    public ResponseEntity<Long> getTotalTimeByUser(
            @PathVariable Long userId,
            @RequestParam(required = false) LocalDate start,
            @RequestParam(required = false) LocalDate end) {

        LocalDate[] range = resolveRange(start, end);

        Long total = shiftRepository
                .sumTotalMinutesByUserInRange(
                        userId,
                        range[0],
                        range[1]
                );

        return ResponseEntity.ok(total);
    }

    @GetMapping("/service/{serviceId}")
    public Page<ShiftDTO> getByServiceInRange(
            @PathVariable Long serviceId,
            @RequestParam(required = false) LocalDate start,
            @RequestParam(required = false) LocalDate end,
            Pageable pageable) {

        LocalDate[] range = resolveRange(start, end);

        return shiftRepository
                .findByServiceIdAndOpenDateBetween(
                        serviceId,
                        range[0],
                        range[1],
                        pageable
                )
                .map(ShiftDTO::new);
    }

    private LocalDate[] resolveRange(LocalDate start, LocalDate end) {

        LocalDate today = LocalDate.now();

        if (end == null)
            end = today;

        if (start == null)
            start = end.minusMonths(1);

        return new LocalDate[]{start, end};
    }

}
