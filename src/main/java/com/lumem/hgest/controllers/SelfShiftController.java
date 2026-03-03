package com.lumem.hgest.controllers;

import com.lumem.hgest.model.shift.Shift;
import com.lumem.hgest.model.shift.ShiftDTO;
import com.lumem.hgest.model.user.StoredUser;
import com.lumem.hgest.security.SecurityUser;
import com.lumem.hgest.repository.ServiceRepository;
import com.lumem.hgest.repository.ShiftRepository;
import com.lumem.hgest.repository.StoredUserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@RestController
@RequestMapping("api/shift/self")
@Transactional
public class SelfShiftController {

    private final ServiceRepository serviceRepository;
    private final StoredUserRepository storedUserRepository;
    private final ShiftRepository shiftRepository;

    public SelfShiftController(ServiceRepository serviceRepository, StoredUserRepository storedUserRepository, ShiftRepository shiftRepository) {

        this.shiftRepository = shiftRepository;
        this.serviceRepository = serviceRepository;
        this.storedUserRepository = storedUserRepository;
    }

    @PostMapping("/open")
    public ResponseEntity<?> openShift(@Valid @RequestBody OpenShiftRequest request){

        long userId = ((SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        StoredUser userRef = storedUserRepository.getReferenceById(userId);

        if (!shiftRepository.hasNoOpenShifts(userId)){
            return ResponseEntity.status(400).body("this user already has an open shift");
        }

        if (!serviceRepository.existsById(request.serviceId()))
            return ResponseEntity.status(400).body("service not found");

        if (serviceRepository.getReferenceById(request.serviceId()).isClosed()){
            return ResponseEntity.status(400).body("already closed");
        }

        Shift shift = new Shift();
        shift.setClosed(false);
        shift.setService(serviceRepository.getReferenceById(request.serviceId));
        shift.setStoredUser(userRef);
        shift.setOpenDate(request.date());
        shift.setOpenTime(request.time());
        shift.setDescription(shift.getDescription());
        shift.setSegment(request.segment());

        shiftRepository.save(shift);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/close/{id}")
    public ResponseEntity<?> closeShift(@PathVariable("id") long id, @Valid @RequestBody CloseShiftRequest request){

        long userId = ((SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();

        if ( !shiftRepository.existsById(id) )
            return ResponseEntity.status(400).body("no such shift found");

        Shift shift = shiftRepository.getReferenceById(id);

        if (shift.getStoredUser().getId() != userId)
            return ResponseEntity.status(400).body("invalid user");

        if (shift.isClosed())
            return ResponseEntity.status(400).body("already closed");

        shift.setClosed(true);

        shift.setCloseDate(request.date());
        shift.setCloseTime(request.time());

        LocalDateTime open = LocalDateTime.of(
                shift.getOpenDate(),
                shift.getOpenTime()
        );


        LocalDateTime close = LocalDateTime.of(
                request.date(),
                request.time()
        );

        long timeElapsed = ChronoUnit.MINUTES.between(open, close);
        shift.setTotalMinutes(timeElapsed);

        shiftRepository.save(shift);

        return ResponseEntity.ok().build();
    }

    @GetMapping("")
    public Page<ShiftDTO> getShifts(@PageableDefault(size = 5) Pageable pageable, @RequestParam(required = false) Boolean closed){

        long id = ((SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();

        Page<Shift> shifts;

        if (closed == null){
            shifts = shiftRepository.findByStoredUserId(id,pageable);
        }
        else {
            shifts = shiftRepository.findByStoredUserIdAndClosed(id,closed,pageable);
        }

        return shifts.map(ShiftDTO::new);

    }


    public record OpenShiftRequest(@NotNull long serviceId,@NotNull LocalDate date,@NotNull LocalTime time, String description, String segment) { }

    public record CloseShiftRequest(@NotNull LocalTime time, @NotNull LocalDate date) {


    }
}
