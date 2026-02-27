package com.lumem.hgest.controllers;

import com.lumem.hgest.model.service.Service;
import com.lumem.hgest.repository.ServiceRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;

@RestController
@RequestMapping("/service")
@Transactional
public class ServiceController {

    private final ServiceRepository serviceRepository;

    public ServiceController(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @PostMapping("/open")
    @PreAuthorize("hasAnyRole('SUPERVISOR','ADMIN','DEV')")
    public ResponseEntity<?> openService(@Valid @RequestBody OpenServiceRequest request){
        Service service = new Service(request.title());
        service.setOpenDate(LocalDate.now());
        service.setOpenTime(LocalTime.now());
        service.setDescription(request.description());
        service.setClosed(false);
        service.setOs(request.os);

        serviceRepository.save(service);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/close/{id}")
    @PreAuthorize("hasAnyRole('SUPERVISOR','ADMIN','DEV')")
    public ResponseEntity<?> closeService(@PathVariable("id") Long id){

        serviceRepository.closeService(id,LocalTime.now(),LocalDate.now());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('SUPERVISOR','ADMIN','DEV')")
    public ResponseEntity<?> update(){
        //TODO
        return null;
    }

    //get by id
    //get by title LIKE
    //get by OS
    //get all* by range
    //get all* closed
    //get all* open


    public record OpenServiceRequest(@NotNull String title, String description,@NotNull Long os) {}

}
