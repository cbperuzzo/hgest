package com.lumem.hgest.controllers;

import com.lumem.hgest.model.Shift;
import com.lumem.hgest.model.StoredUser;
import com.lumem.hgest.model.Util.SecurityUser;
import com.lumem.hgest.repository.ServiceRepository;
import com.lumem.hgest.repository.ShiftRepository;
import com.lumem.hgest.repository.StoredUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@RestController
@RequestMapping("/shift")
@Transactional
public class ShiftController {

    private final ServiceRepository serviceRepository;
    private final StoredUserRepository storedUserRepository;
    private final ShiftRepository shiftRepository;

    public ShiftController( ServiceRepository serviceRepository, StoredUserRepository storedUserRepository,ShiftRepository shiftRepository) {

        this.shiftRepository = shiftRepository;
        this.serviceRepository = serviceRepository;
        this.storedUserRepository = storedUserRepository;
    }

    @PostMapping("/open")
    public ResponseEntity<?> openShift(@RequestBody OpenShiftRequest request){

        long userId = ((SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        StoredUser userRef = storedUserRepository.getReferenceById(userId);

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

    @PostMapping("/close")
    public ResponseEntity<?> closeShift(@RequestBody CloseShiftRequest request){

        long userId = ((SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();

        if ( !shiftRepository.existsById(request.id()) )
            return ResponseEntity.status(400).body("no such shift found");

        Shift shift = shiftRepository.getReferenceById(request.id());

        if (shift.getStoredUser().getId() != userId)
            return ResponseEntity.status(403).body("invalid user");

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

    @PostMapping("/update")
    @PreAuthorize("hasAnyRole('SUPERVISOR','ADMIN','DEV')")
    public ResponseEntity<?> update(){
        return null;
        //TODO
    }

    //get by id
    //get all* by user in range (closed)
    //get total time by user in range (closed)
    //get all* by service

    public record OpenShiftRequest(long serviceId, LocalDate date, LocalTime time, String description, String segment) { }

    public record CloseShiftRequest(long id,LocalTime time, LocalDate date) {


    }
}
