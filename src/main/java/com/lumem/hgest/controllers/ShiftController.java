package com.lumem.hgest.controllers;

import com.lumem.hgest.model.Shift;
import com.lumem.hgest.model.StoredUser;
import com.lumem.hgest.model.Util.SecurityUser;
import com.lumem.hgest.repository.ServiceRepository;
import com.lumem.hgest.repository.StoredUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;

@RestController
@RequestMapping("/shift")
@Transactional
public class ShiftController {

    private final ServiceRepository serviceRepository;
    private final StoredUserRepository storedUserRepository;

    public ShiftController( ServiceRepository serviceRepository, StoredUserRepository storedUserRepository) {

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

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> closeShift(){
        return null;
    }

    public record OpenShiftRequest(long serviceId, LocalDate date, LocalTime time, String description, String segment) { }

    public record CloseShiftRequest(long id) { }
}
